### Telecharger et Installer PostgreSQL 17.RC1

https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

### REFUSER INSTALLATION PROPOSÉ PAR WIZARD À LA FIN DE L'INSTALLATION PRINCIPALE

### Après avoir installé POSTGRES
> Windows > Variable Environnement > Variable Path > MODIFIER > Add New Line > C:\Program Files\PostgreSQL\17\bin

### Connecter en tant que SUPERUSER (postgres - default) dans InteliJ/VSCode

```sql
psql -U postgres

CREATE DATABASE fructose;

CREATE USER team24 WITH LOGIN PASSWORD 'team24';

GRANT ALL PRIVILEGES ON DATABASE fructose TO team24;

GRANT ALL ON SCHEMA public TO team24;

GRANT ALL ON ALL TABLES IN SCHEMA public TO team24;

GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO team24;

GRANT ALL ON ALL FUNCTIONS IN SCHEMA public TO team24;

ALTER ROLE team24 WITH SUPERUSER;

```

### Se connecter avec un nouveau compte
```sql 
psql -U team24 -d fructose
```
### Verifier la connexion
### Executer Spring
### Verifier que le table Users a bien été crée
### Ouvrir une boutelle de champagne et féliciter l'installation de PostgreSQL