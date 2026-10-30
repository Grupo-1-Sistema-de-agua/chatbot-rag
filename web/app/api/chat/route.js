import { verificarLimite } from '../../../lib/limite-preguntas';

const URL_SERVICIO = process.env.CHATBOT_SERVICE_URL ?? 'http://localhost:8082';
const TIEMPO_MAXIMO_MS = 90_000;
const MENSAJE_NO_DISPONIBLE = 'El asistente no está disponible en este momento. Intenta más tarde.';

function obtenerIp(request) {
  const reenviada = request.headers.get('x-forwarded-for');
  if (reenviada) {
    return reenviada.split(',')[0].trim();
  }
  return request.headers.get('x-real-ip') ?? 'desconocida';
}

function responderError(estado, mensaje, encabezados = {}) {
  return Response.json({ mensaje }, { status: estado, headers: encabezados });
}

export async function POST(request) {
  const limite = verificarLimite(obtenerIp(request));
  if (!limite.permitido) {
    return responderError(
      429,
      `Hiciste muchas preguntas seguidas. Intenta de nuevo en ${limite.esperaSegundos} segundos.`,
      { 'Retry-After': String(limite.esperaSegundos) }
    );
  }

  let cuerpo;
  try {
    cuerpo = await request.json();
  } catch {
    return responderError(400, 'La solicitud no es válida.');
  }

  const pregunta = typeof cuerpo?.pregunta === 'string' ? cuerpo.pregunta.trim() : '';
  if (pregunta.length < 3 || pregunta.length > 500) {
    return responderError(400, 'La pregunta debe tener entre 3 y 500 caracteres.');
  }

  let respuesta;
  try {
    respuesta = await fetch(`${URL_SERVICIO}/api/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pregunta }),
      signal: AbortSignal.timeout(TIEMPO_MAXIMO_MS),
      cache: 'no-store',
    });
  } catch (error) {
    console.error('[chat] No se pudo contactar al microservicio:', error.message);
    return responderError(503, MENSAJE_NO_DISPONIBLE);
  }

  if (respuesta.ok) {
    const datos = await respuesta.json();
    return Response.json({ respuesta: datos.respuesta, fuentes: datos.fuentes ?? [] });
  }
  if (respuesta.status === 400) {
    return responderError(400, 'La pregunta no es válida. Revisa que tenga entre 3 y 500 caracteres.');
  }
  if (respuesta.status === 503) {
    return responderError(503, MENSAJE_NO_DISPONIBLE);
  }

  console.error('[chat] Respuesta inesperada del microservicio:', respuesta.status);
  return responderError(502, 'Ocurrió un problema al obtener la respuesta. Intenta de nuevo.');
}