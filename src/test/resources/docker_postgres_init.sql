CREATE TABLE public.gamequeue
(
    id bigint NOT NULL,
    CONSTRAINT gamequeue_pkey PRIMARY KEY (id)
)
    TABLESPACE pg_default;

ALTER TABLE public.gamequeue
    OWNER to postgres;

CREATE TABLE public.player
(
    id bigint NOT NULL,
    userid character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT player_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE public.player
    OWNER to postgres;

CREATE TABLE public.gamequeue_player
(
    gamequeue_id bigint NOT NULL,
    players_id bigint NOT NULL,
    CONSTRAINT gamequeue_player_pkey PRIMARY KEY (gamequeue_id, players_id),
    CONSTRAINT uk_n77stthubigu466ae1es1216c UNIQUE (players_id),
    CONSTRAINT fko34xkf1ijxfr3uds6csot2xkf FOREIGN KEY (gamequeue_id)
        REFERENCES public.gamequeue (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fksp6pejeud2g7p97bj1y8fw5au FOREIGN KEY (players_id)
        REFERENCES public.player (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE public.gamequeue_player
    OWNER to postgres;

