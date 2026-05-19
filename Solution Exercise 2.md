
# Discussion of fields in ontext of existing model

## 'cust_id'
### Description: 
It looks like to be internal ID on the customer Database ( or external ID that customer provides for sync purposes)
### Mapping
Cann't be mapped to existing model
### To dicsuss
Is it nullable (can be used in the future for data sync?)

## 'anrede' 
### Description:
Anrede field ( Dr., Prof., Kind of univserse etc. )
### Mapping
Cann't be mapped to existing model

## 'vorname'
### Description:
Firstname
### Mapping
Map to firstname of contact
### To dicsuss
Is it nullable in customer system? We need @NotNull

## 'nachname'
### Description:
Lastname
### Mapping
Map to lastname of contact
### To dicsuss
Is it nullable in customer system? We need @NotNull

## 'firma'
### Description
Company name
### Mapping
Map to company field of the contact

## 'abt'
### Description
Company department
### Mapping
Cann't be mapped to existing model

## 'email_1'
### Description
Primary email
### Mapping
Map to email field of the contact
### To dicsuss
Is it nullable in customer system? We need @NotNull
Is it possible to have 'email_2' filled out, but 'email_1' null?

## 'tel_mobil'
### Description
Mobile number 
### Mapping
Cann't be mapped to existing model

## 'status_code'
### Description
???
### Mapping
Cann't be mapped to existing model
### To dicsuss
What is the meaning of that field?

## 'last_visit'
### Description
I assume it is the timestamp of last contact to the customer
### Mapping
We map it to activity timestamp
### To dicsuss
Is my assumption right?

## 'last_visit_type'
### Description
I assume it is the type of last contact to the customer
### Mapping
We could map it to activity type, ie for 'VIS' we take MEETING
### To dicsuss
What other values can be used here?

## 'last_visit_note'
### Description
Notice for last last contact to the customer
### Mapping
We could map it to activity description
### To dicsuss
What other values can be used here?

## 'assigned_rep'
### Description
???
### Mapping
Cann't be mapped to existing model
### To dicsuss
What is the meaning of that field?


# Model change suggestions

# Notice

1. Validate field values according to existing model ( like blank, null and combination of firstname, lastname and email)
2. Change the internal app model in smth like

Customer - entity for contact person, could have multiple Contacts (email, tel, pager etc) and multiple Activities
- @NotNull UUID id - auto generated entity id
- @NotNull String 'ext_id' - used for sync, generate one if the customer does not provide any in his model
- @Nullable String 'name_prefix' - it's just a text
- @NotNull String firstname 
- @NotNull String lastname
- @Nullable String company
- @Nullable String department - extended infos
- @Nullable JSON data - all data that can't be mapped directly, but should be visible in CRM. Every sql databse supports such data type

Contact - entity for address book information, how the customer can be contacted 
- @NotNull UUID id - auto generated entity id
- @NotNull ContactType type - enum of ie EMAIL, MOBILE, PAGER (important for germany krankenhäuser), FAX (improtant for germany)
- @NotNull String contact_point - used for email adress or tel number
- @Nullable String notice - i.e. 'Do not call before 11 a.m.'
- booolean primary - if it's a primary contact
We can discuss here 1. conact type-dependent validation 2. contact escalation ie. which contact should be used as first, second etc.

Activity - logs about salesman activities in context of the customer
no need to change anything right now