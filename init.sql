-- =================================================================
-- STEP 1: CREATE ROLES (USERS)
-- =================================================================
CREATE ROLE analyzer_user WITH LOGIN PASSWORD 'analyzer_password';
CREATE ROLE store_user WITH LOGIN PASSWORD 'store_password';
CREATE ROLE cart_user WITH LOGIN PASSWORD 'cart_password';
CREATE ROLE warehouse_user WITH LOGIN PASSWORD 'warehouse_password';


-- =================================================================
-- STEP 2: CREATE SCHEMAS
-- =================================================================
CREATE SCHEMA IF NOT EXISTS analyzer;
CREATE SCHEMA IF NOT EXISTS shopping_cart;
CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE SCHEMA IF NOT EXISTS warehouse;


-- =================================================================
-- SECTION: ANALYZER SCHEMA SETUP
-- =================================================================
GRANT USAGE, CREATE ON SCHEMA analyzer TO analyzer_user;

ALTER DEFAULT PRIVILEGES FOR ROLE analyzer_user IN SCHEMA analyzer
   GRANT ALL ON TABLES TO analyzer_user;
ALTER DEFAULT PRIVILEGES FOR ROLE analyzer_user IN SCHEMA analyzer
   GRANT USAGE, SELECT ON SEQUENCES TO analyzer_user;


SET ROLE analyzer_user;

CREATE TABLE IF NOT EXISTS analyzer.scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name VARCHAR,
    UNIQUE(hub_id, name)
);

CREATE TABLE IF NOT EXISTS analyzer.sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

CREATE TABLE IF NOT EXISTS analyzer.conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    operation VARCHAR,
    value INTEGER
);

CREATE TABLE IF NOT EXISTS analyzer.actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    value INTEGER
);

CREATE TABLE IF NOT EXISTS analyzer.scenario_conditions (
    scenario_id BIGINT REFERENCES analyzer.scenarios(id),
    sensor_id VARCHAR REFERENCES analyzer.sensors(id),
    condition_id BIGINT REFERENCES analyzer.conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

CREATE TABLE IF NOT EXISTS analyzer.scenario_actions (
    scenario_id BIGINT REFERENCES analyzer.scenarios(id),
    sensor_id VARCHAR REFERENCES analyzer.sensors(id),
    action_id BIGINT REFERENCES analyzer.actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

CREATE OR REPLACE FUNCTION analyzer.check_hub_id()
RETURNS TRIGGER AS
'
BEGIN
    IF (SELECT hub_id FROM analyzer.scenarios WHERE id = NEW.scenario_id) != (SELECT hub_id FROM analyzer.sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION ''Hub IDs do not match for scenario_id % and sensor_id %'', NEW.scenario_id, NEW.sensor_id;
    END IF;
    RETURN NEW;
END;
'
LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON analyzer.scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION analyzer.check_hub_id();

CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON analyzer.scenario_actions
FOR EACH ROW
EXECUTE FUNCTION analyzer.check_hub_id();

RESET ROLE;


GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA analyzer TO analyzer_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA analyzer TO analyzer_user;


-- =================================================================
-- SECTION: SHOPPING CART SCHEMA SETUP
-- =================================================================
GRANT USAGE, CREATE ON SCHEMA shopping_cart TO cart_user;

ALTER DEFAULT PRIVILEGES FOR ROLE cart_user IN SCHEMA shopping_cart
   GRANT ALL ON TABLES TO cart_user;
ALTER DEFAULT PRIVILEGES FOR ROLE cart_user IN SCHEMA shopping_cart
   GRANT USAGE, SELECT ON SEQUENCES TO cart_user;

SET ROLE cart_user;

-- ... Tables for shopping_cart schema will go here ...

RESET ROLE;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_cart TO cart_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA shopping_cart TO cart_user;


-- =================================================================
-- SECTION: SHOPPING STORE SCHEMA SETUP
-- =================================================================
GRANT USAGE, CREATE ON SCHEMA shopping_store TO store_user;

ALTER DEFAULT PRIVILEGES FOR ROLE store_user IN SCHEMA shopping_store
   GRANT ALL ON TABLES TO store_user;
ALTER DEFAULT PRIVILEGES FOR ROLE store_user IN SCHEMA shopping_store
   GRANT USAGE, SELECT ON SEQUENCES TO store_user;

SET ROLE store_user;

CREATE TYPE shopping_store.product_category AS ENUM ('LIGHTING', 'CONTROL', 'SENSORS');
CREATE TYPE shopping_store.product_state AS ENUM ('ACTIVE', 'DEACTIVATE');
CREATE TYPE shopping_store.quantity_state AS ENUM ('ENDED', 'FEW', 'ENOUGH', 'MANY');

CREATE TABLE IF NOT EXISTS shopping_store.products
(
    product_id       UUID PRIMARY KEY,

    name             VARCHAR(255) NOT NULL UNIQUE,
    description      TEXT,
    image_src        VARCHAR(255),

    category         shopping_store.product_category NOT NULL,
    product_state    shopping_store.product_state    NOT NULL,
    quantity_state   shopping_store.quantity_state   NOT NULL,

    price            NUMERIC(38,2) NOT NULL CHECK (price > 0)
);

RESET ROLE;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_store TO store_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA shopping_store TO store_user;


-- =================================================================
-- SECTION: WAREHOUSE SCHEMA SETUP
-- =================================================================
GRANT USAGE, CREATE ON SCHEMA warehouse TO warehouse_user;

ALTER DEFAULT PRIVILEGES FOR ROLE warehouse_user IN SCHEMA warehouse
   GRANT ALL ON TABLES TO warehouse_user;
ALTER DEFAULT PRIVILEGES FOR ROLE warehouse_user IN SCHEMA warehouse
   GRANT USAGE, SELECT ON SEQUENCES TO warehouse_user;

SET ROLE warehouse_user;

CREATE TABLE IF NOT EXISTS warehouse.warehouse_items
(
    product_id  UUID PRIMARY KEY,

    quantity    INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),

    weight_kg   NUMERIC(10, 3) NOT NULL CHECK (weight_kg > 0),
    width_m     NUMERIC(10, 3) NOT NULL CHECK (width_m > 0),
    height_m    NUMERIC(10, 3) NOT NULL CHECK (height_m > 0),
    depth_m     NUMERIC(10, 3) NOT NULL CHECK (depth_m > 0),
    is_fragile  BOOLEAN NOT NULL DEFAULT FALSE
);

RESET ROLE;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA warehouse TO warehouse_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA warehouse TO warehouse_user;