'use client';

import { useEffect, useRef, useState } from 'react';
import estilos from './ChatWidget.module.css';

const SUGERENCIAS = [
  '¿Cuánto cuesta la media paja?',
  '¿Cómo sé si tengo una fuga?',
  '¿Qué formas de pago aceptan?',
];

const BIENVENIDA = {
  rol: 'asistente',
  texto: 'Hola, soy el asistente virtual de la oficina del agua. Puedo ayudarte con tarifas, cálculo del cobro, lecturas del contador, pagos, fugas y trámites.',
  fuentes: [],
};

export default function ChatWidget() {
  const [abierto, setAbierto] = useState(false);
  const [mensajes, setMensajes] = useState([BIENVENIDA]);
  const [texto, setTexto] = useState('');
  const [cargando, setCargando] = useState(false);
  const finRef = useRef(null);

  useEffect(() => {
    finRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [mensajes, cargando]);

  async function enviar(pregunta) {
    const limpia = pregunta.trim();
    if (limpia.length < 3 || cargando) {
      return;
    }

    setMensajes((anteriores) => [...anteriores, { rol: 'vecino', texto: limpia, fuentes: [] }]);
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
        setMensajes((anteriores) => [
          ...anteriores,
          { rol: 'asistente', texto: datos.respuesta, fuentes: datos.fuentes ?? [] },
        ]);
      } else {
        setMensajes((anteriores) => [
          ...anteriores,
          { rol: 'error', texto: datos.mensaje ?? 'Ocurrió un problema. Intenta de nuevo.', fuentes: [] },
        ]);
      }
    } catch {
      setMensajes((anteriores) => [
        ...anteriores,
        { rol: 'error', texto: 'No hay conexión. Revisa tu internet e intenta de nuevo.', fuentes: [] },
      ]);
    } finally {
      setCargando(false);
    }
  }

  function alEnviar(evento) {
    evento.preventDefault();
    enviar(texto);
  }

  return (
    <>
      {abierto && (
        <section className={estilos.panel} aria-label="Asistente virtual de la oficina del agua">
          <header className={estilos.encabezado}>
            <div>
              <p className={estilos.titulo}>Asistente virtual</p>
              <p className={estilos.subtitulo}>Oficina del agua</p>
            </div>
            <button
              type="button"
              className={estilos.cerrar}
              onClick={() => setAbierto(false)}
              aria-label="Cerrar el chat"
            >
              ×
            </button>
          </header>

          <div className={estilos.mensajes}>
            {mensajes.map((mensaje, indice) => (
              <div key={indice} className={`${estilos.mensaje} ${estilos[mensaje.rol]}`}>
                <p>{mensaje.texto}</p>
                {mensaje.fuentes.length > 0 && (
                  <p className={estilos.fuentes}>Fuentes: {mensaje.fuentes.join(', ')}</p>
                )}
              </div>
            ))}

            {mensajes.length === 1 && (
              <div className={estilos.sugerencias}>
                {SUGERENCIAS.map((sugerencia) => (
                  <button
                    key={sugerencia}
                    type="button"
                    className={estilos.sugerencia}
                    onClick={() => enviar(sugerencia)}
                  >
                    {sugerencia}
                  </button>
                ))}
              </div>
            )}

            {cargando && (
              <div className={`${estilos.mensaje} ${estilos.asistente} ${estilos.escribiendo}`}>
                <p>Escribiendo…</p>
              </div>
            )}
            <div ref={finRef} />
          </div>

          <form className={estilos.formulario} onSubmit={alEnviar}>
            <input
              className={estilos.entrada}
              value={texto}
              onChange={(evento) => setTexto(evento.target.value)}
              placeholder="Escribe tu pregunta…"
              maxLength={500}
              disabled={cargando}
              aria-label="Tu pregunta"
            />
            <button
              type="submit"
              className={estilos.enviar}
              disabled={cargando || texto.trim().length < 3}
            >
              Enviar
            </button>
          </form>
        </section>
      )}

      <button
        type="button"
        className={estilos.boton}
        onClick={() => setAbierto((valor) => !valor)}
        aria-label={abierto ? 'Cerrar el chat' : 'Abrir el chat'}
      >
        {abierto ? '×' : '💬'}
      </button>
    </>
  );
}