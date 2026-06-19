# ERD Review

## 1. Product → Store
Use store_id instead of merchant_id.

Reason:
Product belongs to Store.
---

## 2. Users and Roles
Use users, roles and user_roles tables.

Reason:
ADMIN and MERCHANT can be managed through roles.
---

## 3. User Store Access
Add user_store_access table.

Reason:
Merchant should access only assigned stores.