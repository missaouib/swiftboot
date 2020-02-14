CREATE VIEW `V_ADMIN_USER_PERMISSIONS` AS SELECT D.ID, A.LOGIN_NAME, A.USER_NAME, B.ROLE_NAME, B.ROLE_DESC, D.PERM_CODE, D.PERM_DESC FROM DEMO_ADMIN_USER A, DEMO_ADMIN_ROLE B, DEMO_ADMIN_USER_ROLE_REL C, DEMO_ADMIN_PERMISSION D, DEMO_ADMIN_ROLE_PERMISSION_REL E WHERE A.ID = C.ADMIN_USER_ID AND B.ID = C.ADMIN_ROLE_ID AND B.ID = E.ADMIN_ROLE_ID AND D.ID = E.ADMIN_PERMISSION_ID;