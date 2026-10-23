'use client';

import { useEffect, useRef, useState } from 'react';
import estilos from './ChatAsistente.module.css';

const SUGERENCIAS = [
  {
    icono: 'fa-tags',
    titulo: 'Tarifas vigentes',
    descripcion: 'Cuotas de cuarto y media paja',
    pregunta: '¿Cuánto cuesta el servicio de agua?',
  },
  {
    icono: 'fa-calculator',
    titulo: 'Calcular mi cobro',
    descripcion: 'Con tu tipo de servicio y consumo',
    pregunta: 'Tengo cuarto de paja y consumí 20,000 litros, ¿cuánto pago?',
  },
  {
    icono: 'fa-droplet',
    titulo: 'Detectar una fuga',
    descripcion: 'Pasos para revisar tu contador',
    pregunta: '¿Cómo sé si tengo una fuga?',
  },
  {
    icono: 'fa-clock',
    titulo: 'Horario de atención',
    descripcion: 'Cuándo visitar la oficina',
    pregunta: '¿En qué horario atienden?',
  },
];

const PREGUNTAS_FRECUENTES = [
  { icono: 'fa-money-bill-wave', texto: 'Formas de pago', pregunta: '¿Qué formas de pago aceptan?' },
  { icono: 'fa-map-marker-alt', texto: 'Sectores con servicio', pregunta: '¿En qué sectores hay servicio?' },
  { icono: 'fa-tachometer-alt', texto: 'Lectura del contador', pregunta: '¿Cada cuánto toman la lectura del contador?' },
  { icono: 'fa-receipt', texto: 'Recibo de pago', pregunta: '¿Cómo es el recibo de pago?' },
  { icono: 'fa-file-signature', texto: 'Nueva conexión', pregunta: '¿Cómo solicito una nueva conexión de agua?' },
  { icono: 'fa-user-edit', texto: 'Cambio de titular', pregunta: '¿Cómo cambio el titular del servicio?' },
];

const FUENTES_MARKDOWN = {
  'calculo-consumo.md': 'Cálculo del consumo',
  'como-pagar.md': 'Cómo pagar',
  'fugas.md': 'Fugas de agua',
  'lecturas.md': 'Lecturas del contador',
  'oficina.md': 'Información de la oficina',
};

function describirFuente(fuente) {
  if (FUENTES_MARKDOWN[fuente]) {
    return { nombre: FUENTES_MARKDOWN[fuente], icono: 'fa-file-alt' };
  }
  return { nombre: fuente, icono: 'fa-database' };
}

function horaActual() {
  return new Date().toLocaleTimeString('es-GT', { hour: '2-digit', minute: '2-digit' });
}

function Orbe({ grande }) {
  return (
    <div className={`${estilos.orbe} ${grande ? estilos.orbeGrande : estilos.orbePequeno}`} aria-hidden="true">
      <span className={estilos.orbeAnillo} />
      <span className={estilos.orbeNucleo}>
        <svg viewBox="0 0 100 100" className={estilos.orbeGota}>
          <path
            d="M50 14 C50 14 26 44 26 62 C26 77 37 88 50 88 C63 88 74 77 74 62 C74 44 50 14 50 14 Z"
            fill="#ffffff"
          />
          <ellipse cx="42" cy="62" rx="5" ry="8" fill="#023e7d" opacity="0.25" />
        </svg>
      </span>
    </div>
  );
}

function Mensaje({ mensaje, copiado, alCopiar }) {
  if (mensaje.rol === 'vecino') {
    return (
      <div className={`${estilos.fila} ${estilos.filaVecino}`}>
        <div className={`${estilos.burbuja} ${estilos.burbujaVecino}`}>
          <p>{mensaje.texto}</p>
          <span className={estilos.hora}>{mensaje.hora}</span>
        </div>
      </div>
    );
  }

  const esError = mensaje.rol === 'error';
  return (
    <div className={estilos.fila}>
      <div className={`${estilos.avatar} ${esError ? estilos.avatarError : ''}`} aria-hidden="true">
        <i className={`fas ${esError ? 'fa-exclamation-triangle' : 'fa-droplet'}`} />
      </div>
      <div className={`${estilos.burbuja} ${esError ? estilos.burbujaError : estilos.burbujaAsistente}`}>
        <p>{mensaje.texto}</p>

        {mensaje.fuentes.length > 0 && (
          <div className={estilos.fuentes}>
            {mensaje.fuentes.map((fuente) => {
              const descripcion = describirFuente(fuente);
              return (
                <span key={fuente} className={estilos.fuente}>
                  <i className={`fas ${descripcion.icono}`} aria-hidden="true" />
                  {descripcion.nombre}
                </span>
              );
            })}
          </div>
        )}

        <div className={estilos.pieMensaje}>
          <span className={estilos.hora}>{mensaje.hora}</span>
          {!esError && (
            <button
              type="button"
              className={estilos.copiar}
              onClick={() => alCopiar(mensaje)}
              aria-label="Copiar respuesta"
            >
              <i className={`fas ${copiado ? 'fa-check' : 'fa-copy'}`} aria-hidden="true" />
              {copiado ? 'Copiado' : 'Copiar'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default function ChatAsistente() {
  const [mensajes, setMensajes] = useState([]);
  const [texto, setTexto] = useState('');
  const [cargando, setCargando] = useState(false);
  const [copiado, setCopiado] = useState(null);
  const finRef = useRef(null);
  const entradaRef = useRef(null);
  const siguienteId = useRef(1);

  const conversando = mensajes.length > 0;

  useEffect(() => {
    if (conversando) {
      finRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }
  }, [mensajes, cargando, conversando]);

  function agregar(rol, contenido, fuentes = []) {
    const mensaje = { id: siguienteId.current++, rol, texto: contenido, fuentes, hora: horaActual() };
    setMensajes((anteriores) => [...anteriores, mensaje]);
  }

  async function enviar(pregunta) {
    const limpia = pregunta.trim();
    if (limpia.length < 3 || cargando) {
      return;
    }

    agregar('vecino', limpia);
    setTexto('');
    setCargando(true);

    try {
      const respuesta = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pregunta: limpia }),
      });
      const datos = await respuesta.json().catch(() => ({}));

      if (respuesta.ok) {
        agregar('asistente', datos.respuesta, datos.fuentes ?? []);
      } else {
        agregar('error', datos.mensaje ?? 'Ocurrió un problema. Intenta de nuevo.');
      }
    } catch {
      agregar('error', 'No hay conexión. Revisa tu internet e intenta de nuevo.');
    } finally {
      setCargando(false);
      entradaRef.current?.focus();
    }
  }

  function alEnviar(evento) {
    evento.preventDefault();
    enviar(texto);
  }

  function nuevaConversacion() {
    setMensajes([]);
    setTexto('');
    entradaRef.current?.focus();
  }

  async function copiar(mensaje) {
    try {
      await navigator.clipboard.writeText(mensaje.texto);
      setCopiado(mensaje.id);
      setTimeout(() => setCopiado(null), 1500);
    } catch {
      // El navegador no permitió copiar.
    }
  }

  return (
    <div className={estilos.distribucion}>
      <section
        className={`${estilos.chat} ${conversando ? estilos.chatActivo : ''}`}
        aria-label="Asistente virtual de la oficina del agua"
      >
        {conversando && (
          <header className={estilos.encabezado}>
            <div className={estilos.identidad}>
              <Orbe />
              <div>
                <p className={estilos.nombre}>Asistente del agua</p>
                <p className={estilos.estado}>
                  <span className={estilos.punto} />
                  En línea · responde con información oficial
                </p>
              </div>
            </div>
            <button type="button" className={estilos.botonNueva} onClick={nuevaConversacion}>
              <i className="fas fa-rotate-left" aria-hidden="true" />
              <span>Nueva conversación</span>
            </button>
          </header>
        )}

        {conversando ? (
          <div className={estilos.mensajes} aria-live="polite">
            {mensajes.map((mensaje) => (
              <Mensaje
                key={mensaje.id}
                mensaje={mensaje}
                copiado={copiado === mensaje.id}
                alCopiar={copiar}
              />
            ))}

            {cargando && (
              <div className={estilos.fila}>
                <div className={estilos.avatar} aria-hidden="true">
                  <i className="fas fa-droplet" />
                </div>
                <div className={`${estilos.burbuja} ${estilos.burbujaAsistente} ${estilos.escribiendo}`}>
                  <span />
                  <span />
                  <span />
                </div>
              </div>
            )}
            <div ref={finRef} />
          </div>
        ) : (
          <div className={estilos.bienvenida}>
            <Orbe grande />
            <p className={estilos.saludo}>Hola, vecino</p>
            <h1 className={estilos.titular}>¿En qué te puedo ayudar hoy?</h1>
            <p className={estilos.descripcion}>
              Pregúntame sobre tarifas, el cálculo de tu cobro, lecturas, pagos, fugas o trámites.
              Respondo con la información oficial de la oficina del agua.
            </p>
            <div className={estilos.sugerencias}>
              {SUGERENCIAS.map((sugerencia) => (
                <button
                  key={sugerencia.titulo}
                  type="button"
                  className={estilos.sugerencia}
                  onClick={() => enviar(sugerencia.pregunta)}
                >
                  <span className={estilos.sugerenciaIcono}>
                    <i className={`fas ${sugerencia.icono}`} aria-hidden="true" />
                  </span>
                  <span className={estilos.sugerenciaTitulo}>{sugerencia.titulo}</span>
                  <span className={estilos.sugerenciaTexto}>{sugerencia.descripcion}</span>
                </button>
              ))}
            </div>
          </div>
        )}

        <form className={estilos.formulario} onSubmit={alEnviar}>
          <div className={estilos.caja}>
            <i className={`fas fa-droplet ${estilos.cajaIcono}`} aria-hidden="true" />
            <input
              ref={entradaRef}
              className={estilos.entrada}
              value={texto}
              onChange={(evento) => setTexto(evento.target.value)}
              placeholder="Escribe tu pregunta sobre el servicio de agua…"
              maxLength={500}
              disabled={cargando}
              aria-label="Tu pregunta"
            />
            {texto.length > 400 && <span className={estilos.contador}>{texto.length}/500</span>}
            <button
              type="submit"
              className={estilos.enviar}
              disabled={cargando || texto.trim().length < 3}
              aria-label="Enviar pregunta"
            >
              <i className="fas fa-arrow-up" aria-hidden="true" />
            </button>
          </div>
          <p className={estilos.aviso}>
            El asistente puede equivocarse. Para deudas o datos personales, acércate a la oficina.
          </p>
        </form>
      </section>

      <aside className={estilos.lateral}>
        <div className={estilos.tarjetaDestacada}>
          <svg className={estilos.marcaAgua} viewBox="0 0 100 100" aria-hidden="true">
            <path
              d="M50 8 C50 8 22 42 22 62 C22 79 34 92 50 92 C66 92 78 79 78 62 C78 42 50 8 50 8 Z"
              fill="#ffffff"
            />
          </svg>
          <p className={estilos.destacadaEtiqueta}>Respuestas con datos oficiales</p>
          <p className={estilos.destacadaTitulo}>Tarifas y catálogos al día</p>
          <p className={estilos.destacadaTexto}>
            Las tarifas, los sectores y los métodos de pago se consultan del sistema de la oficina.
            Cada respuesta indica de dónde salió.
          </p>
        </div>

        <div className={estilos.tarjeta}>
          <p className={estilos.tarjetaTitulo}>Preguntas frecuentes</p>
          <ul className={estilos.listaAcciones}>
            {PREGUNTAS_FRECUENTES.map((frecuente) => (
              <li key={frecuente.pregunta}>
                <button
                  type="button"
                  className={estilos.accion}
                  onClick={() => enviar(frecuente.pregunta)}
                  disabled={cargando}
                >
                  <i className={`fas ${frecuente.icono} ${estilos.accionIcono}`} aria-hidden="true" />
                  <span>{frecuente.texto}</span>
                  <i className={`fas fa-arrow-right ${estilos.accionFlecha}`} aria-hidden="true" />
                </button>
              </li>
            ))}
          </ul>
        </div>

        <div className={`${estilos.tarjeta} ${estilos.tarjetaPrivacidad}`}>
          <i className="fas fa-shield-alt" aria-hidden="true" />
          <div>
            <p className={estilos.tarjetaTitulo}>Tu información está protegida</p>
            <p className={estilos.tarjetaTexto}>
              El asistente no consulta deudas, pagos ni datos personales. Esa información solo se da en la oficina.
            </p>
          </div>
        </div>
      </aside>
    </div>
  );
}