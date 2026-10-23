const VENTANA_MS = 60_000;
const MAXIMO = Number(process.env.CHAT_LIMITE_POR_MINUTO ?? 10);
const solicitudes = new Map();

export function verificarLimite(ip) {
  const ahora = Date.now();
  const recientes = (solicitudes.get(ip) ?? []).filter((tiempo) => ahora - tiempo < VENTANA_MS);

  if (recientes.length >= MAXIMO) {
    solicitudes.set(ip, recientes);
    const esperaSegundos = Math.ceil((VENTANA_MS - (ahora - recientes[0])) / 1000);
    return { permitido: false, esperaSegundos };
  }

  recientes.push(ahora);
  solicitudes.set(ip, recientes);
  if (solicitudes.size > 5000) {
    limpiar(ahora);
  }
  return { permitido: true, esperaSegundos: 0 };
}

function limpiar(ahora) {
  for (const [ip, tiempos] of solicitudes) {
    if (tiempos.every((tiempo) => ahora - tiempo >= VENTANA_MS)) {
      solicitudes.delete(ip);
    }
  }
}