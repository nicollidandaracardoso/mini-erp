CREATE TABLE cliente (
                         id         BIGSERIAL PRIMARY KEY,
                         nome       VARCHAR(120) NOT NULL,
                         email      VARCHAR(160) NOT NULL UNIQUE,
                         criado_em  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE produto (
                         id                  BIGSERIAL PRIMARY KEY,
                         nome                VARCHAR(120)  NOT NULL,
                         preco               NUMERIC(12,2) NOT NULL CHECK (preco >= 0),
                         quantidade_estoque  INTEGER       NOT NULL CHECK (quantidade_estoque >= 0)
);

CREATE TABLE pedido (
                        id          BIGSERIAL PRIMARY KEY,
                        cliente_id  BIGINT        NOT NULL REFERENCES cliente (id),
                        status      VARCHAR(20)   NOT NULL,
                        total       NUMERIC(12,2) NOT NULL,
                        criado_em   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE item_pedido (
                             id              BIGSERIAL PRIMARY KEY,
                             pedido_id       BIGINT        NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
                             produto_id      BIGINT        NOT NULL REFERENCES produto (id),
                             quantidade      INTEGER       NOT NULL CHECK (quantidade > 0),
                             preco_unitario  NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_pedido_cliente ON pedido (cliente_id);
CREATE INDEX idx_item_pedido_pedido ON item_pedido (pedido_id);