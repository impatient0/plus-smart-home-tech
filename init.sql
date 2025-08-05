-- создаём схемы
CREATE SCHEMA IF NOT EXISTS analyzer;
CREATE SCHEMA IF NOT EXISTS shopping_cart;
CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE SCHEMA IF NOT EXISTS warehouse;

-- создаём пользователей
CREATE ROLE analyzer_user WITH LOGIN PASSWORD 'analyzer_password';
CREATE ROLE store_user WITH LOGIN PASSWORD 'store_password';
CREATE ROLE cart_user WITH LOGIN PASSWORD 'cart_password';
CREATE ROLE warehouse_user WITH LOGIN PASSWORD 'warehouse_password';

-- выдаём пользователям привилегии на их схемах
GRANT ALL PRIVILEGES ON SCHEMA analyzer TO analyzer_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA analyzer TO analyzer_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA analyzer GRANT ALL ON TABLES TO analyzer_user;

GRANT ALL PRIVILEGES ON SCHEMA shopping_cart TO cart_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_cart TO cart_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA shopping_cart GRANT ALL ON TABLES TO cart_user;

GRANT ALL PRIVILEGES ON SCHEMA shopping_store TO store_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_store TO store_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA shopping_store GRANT ALL ON TABLES TO store_user;

GRANT ALL PRIVILEGES ON SCHEMA warehouse TO warehouse_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA warehouse TO warehouse_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA warehouse GRANT ALL ON TABLES TO warehouse_user;

-- создаём таблицу scenarios в схеме analyzer
CREATE TABLE IF NOT EXISTS analyzer.scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name VARCHAR,
    UNIQUE(hub_id, name)
);

-- создаём таблицу sensors в схеме analyzer
CREATE TABLE IF NOT EXISTS analyzer.sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

-- создаём таблицу conditions в схеме analyzer
CREATE TABLE IF NOT EXISTS analyzer.conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    operation VARCHAR,
    value INTEGER
);

-- создаём таблицу actions в схеме analyzer
CREATE TABLE IF NOT EXISTS analyzer.actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR,
    value INTEGER
);

-- создаём таблицу scenario_conditions в схеме analyzer, связывающую сценарий, датчик и условие активации сценария
CREATE TABLE IF NOT EXISTS analyzer.scenario_conditions (
    scenario_id BIGINT REFERENCES analyzer.scenarios(id),
    sensor_id VARCHAR REFERENCES analyzer.sensors(id),
    condition_id BIGINT REFERENCES analyzer.conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
);

-- создаём таблицу scenario_actions в схеме analyzer, связывающую сценарий, датчик и действие, которое нужно выполнить при активации сценария
CREATE TABLE IF NOT EXISTS analyzer.scenario_actions (
    scenario_id BIGINT REFERENCES analyzer.scenarios(id),
    sensor_id VARCHAR REFERENCES analyzer.sensors(id),
    action_id BIGINT REFERENCES analyzer.actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
);

-- создаём функцию для проверки, что связываемые сценарий и датчик работают с одним и тем же хабом, в схеме analyzer
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

-- создаём триггер, проверяющий, что «условие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON analyzer.scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION analyzer.check_hub_id();

-- создаём триггер, проверяющий, что «действие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON analyzer.scenario_actions
FOR EACH ROW
EXECUTE FUNCTION analyzer.check_hub_id();