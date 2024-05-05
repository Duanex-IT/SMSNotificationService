alter table
   ns_sms_request
drop column LINKEDMESSAGE_MESSAGEID;

alter table
   ns_sms_request
add
   (
      customerId varchar2(255 char)
   );