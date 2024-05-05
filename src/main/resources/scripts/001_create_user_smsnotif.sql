/*
   User BD: SYSTEM
   Создание:
   - пользвателя БД smsnotif для создания объектов БД;
*/


create tablespace smsnotif 
  datafile  '/opt/db/esbprd/smsnotif.dbf'
  size 1048576000 autoextend on next 52428800 maxsize 32767m
  logging online permanent blocksize 8192
  extent management 
  local autoallocate 
  default nocompress  segment space management auto;


-- Create the user  
create user smsnotif
  identified by Geen$type91
  default tablespace smsnotif
  quota 100M on smsnotif
  temporary tablespace TEMP
  profile DEFAULT;


-- Grant/Revoke system privileges 
grant alter session to smsnotif;
grant create cluster to smsnotif;
grant create database link to smsnotif;
grant create dimension to smsnotif;
grant create indextype to smsnotif;
grant create materialized view to smsnotif;
grant create operator to smsnotif;
grant create procedure to smsnotif;
grant create sequence to smsnotif;
grant create session to smsnotif;
grant create synonym to smsnotif;
grant create table to smsnotif;
grant create trigger to smsnotif;
grant create type to smsnotif;
grant create view to smsnotif;
grant on commit refresh to smsnotif;
grant query rewrite to smsnotif;
grant create role to smsnotif;



commit;


