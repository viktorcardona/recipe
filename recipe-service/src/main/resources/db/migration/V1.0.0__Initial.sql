DROP TABLE IF EXISTS ingredient;
DROP TABLE IF EXISTS recipe;
DROP TABLE IF EXISTS unit_of_measure;

CREATE TABLE unit_of_measure (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT unit_of_measure_name UNIQUE(name)
);

CREATE TABLE recipe (
    id BIGSERIAL PRIMARY KEY,
    lock_version bigint NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    dish_type VARCHAR(50) NOT NULL,
    servings INTEGER NOT NULL,
    instructions TEXT NOT NULL
);

CREATE INDEX idx_recipe_dish_type ON recipe (dish_type);
CREATE INDEX idx_recipe_servings ON recipe (servings);
CREATE INDEX idx_recipe_instructions ON recipe USING GIN (to_tsvector('english', instructions));

CREATE TABLE ingredient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    amount DECIMAL NOT NULL,
    unit_of_measure_id BIGINT NOT NULL REFERENCES unit_of_measure(id),
    recipe_id BIGINT NOT NULL REFERENCES recipe(id),
    UNIQUE (name, recipe_id)
);

CREATE INDEX idx_ingredient_name ON ingredient (name);
CREATE INDEX idx_ingredient_recipe_id ON ingredient (recipe_id);
