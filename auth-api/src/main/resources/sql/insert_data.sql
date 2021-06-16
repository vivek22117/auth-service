-- insert values in role
INSERT INTO role (id, name, description) VALUES (1, 'ADMIN', 'Administration');
INSERT INTO role (id, name, description) VALUES (2, 'USER', 'Customer');
INSERT INTO role (id, name, description) VALUES (3, 'GUEST', 'Visitor');



-- insert values in permissions
INSERT INTO permission (id, name, description) VALUES (1, 'READ', 'Retrieve data');
INSERT INTO permission (id, name, description) VALUES (2, 'UPDATE', 'Update data');
INSERT INTO permission (id, name, description) VALUES (3, 'DELETE', 'Delete records');
INSERT INTO permission (id, name, description) VALUES (4, 'ADD', 'Insert new records');