alter table
   ns_sms_request
add
   (
      version number(19,0)
   );

ALTER TABLE ns_sms_request
  MODIFY (version NOT NULL DISABLE);

update ns_sms_request set version=1;

SELECT constraint_name
    FROM user_constraints
    WHERE table_name = 'NS_SMS_REQUEST' and status='DISABLED' and constraint_type='C'
==>
ALTER TABLE ns_sms_request ENABLE CONSTRAINT SYS_C004712;