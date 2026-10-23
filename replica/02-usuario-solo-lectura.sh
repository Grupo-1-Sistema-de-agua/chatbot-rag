#!/bin/bash
# Crea el usuario de solo lectura que usa el chatbot.
# La contraseña viene de la variable REPLICA_RO_PASSWORD (definida en el archivo .env).
# Solo tiene permiso SELECT sobre catálogos públicos (tarifas, tipos de servicio,
# sectores y métodos de pago): no puede escribir ni leer datos de clientes,
# contadores, lecturas, pagos o usuarios.
set -e

if [ -z "$REPLICA_RO_PASSWORD" ]; then
  echo "ERROR: falta la variable REPLICA_RO_PASSWORD" >&2
  exit 1
fi

mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" <<SQL
CREATE USER IF NOT EXISTS 'chatbot_ro'@'%' IDENTIFIED BY '${REPLICA_RO_PASSWORD}';
GRANT SELECT ON agua_db.Tb_Tipos_Servicios TO 'chatbot_ro'@'%';
GRANT SELECT ON agua_db.Tb_Tarifas TO 'chatbot_ro'@'%';
GRANT SELECT ON agua_db.Tb_Sectores TO 'chatbot_ro'@'%';
GRANT SELECT ON agua_db.Tb_Metodos_Pago TO 'chatbot_ro'@'%';
FLUSH PRIVILEGES;
SQL

echo "Usuario chatbot_ro creado con permisos de solo lectura sobre catálogos públicos."
