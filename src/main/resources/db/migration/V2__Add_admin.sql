INSERT INTO usr (id, username, password, active) VALUES (1, 'Admin', '$2a$08$HnkpDFCPGyc3sWBt1IFcPOVGx1ZGlWlunGFmqJJxHjqZwM6x2/DXi', true);
INSERT INTO user_role (user_id, roles) VALUES (1, 'USER'), (1, 'ADMIN');
