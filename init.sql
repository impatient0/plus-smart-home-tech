-- =================================================================
-- STEP 1: CREATE ROLES (USERS)
-- =================================================================
CREATE ROLE analyzer_user WITH LOGIN PASSWORD 'analyzer_password';


-- =================================================================
-- STEP 2: CREATE SCHEMAS
-- =================================================================
CREATE SCHEMA IF NOT EXISTS analyzer;


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
-- SECTION: CREATE COMMERCE DATABASES
-- =================================================================
CREATE DATABASE commerce_shopping_cart;
CREATE DATABASE commerce_shopping_store;
CREATE DATABASE commerce_warehouse;
CREATE DATABASE commerce_order;
CREATE DATABASE commerce_delivery;
CREATE DATABASE commerce_payment;