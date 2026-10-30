'use client';

import { useEffect, useState } from 'react';
import estilos from './BarraSuperior.module.css';

export default function BarraSuperior() {
  const [oscuro, setOscuro] = useState(false);

  useEffect(() => {
    setOscuro(document.documentElement.getAttribute('data-tema') === 'oscuro');
  }, []);

  function alternarTema() {
    const nuevo = !oscuro;
    setOscuro(nuevo);
    document.documentElement.setAttribute('data-tema', nuevo ? 'oscuro' : 'claro');
    try {
      localStorage.setItem('tema', nuevo ? 'oscuro' : 'claro');
    } catch {
      // Sin almacenamiento disponible: el tema solo dura esta visita.
    }
  }

  return (
    <header className={estilos.barra}>
      <div className={estilos.contenido}>
        <div className={estilos.marca}>
          <i className="fas fa-droplet" aria-hidden="true" />
          <strong>Oficina del Agua</strong>
          <span className={estilos.etiqueta}>Asistente virtual</span>
        </div>
        <button
          type="button"
          className={estilos.botonIcono}
          onClick={alternarTema}
          title="Cambiar tema"
          aria-label="Cambiar tema"
        >
          <i className={oscuro ? 'fas fa-sun' : 'fas fa-moon'} aria-hidden="true" />
        </button>
      </div>
    </header>
  );
}