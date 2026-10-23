import ChatWidget from './_components/ChatWidget';
import estilos from './page.module.css';

const SECCIONES = [
  {
    titulo: 'Tarifas y cobro',
    texto: 'Conoce las cuotas de cuarto de paja y media paja, los litros incluidos y cómo se calcula el exceso de consumo.',
  },
  {
    titulo: 'Pagos y recibos',
    texto: 'Consulta los métodos de pago que acepta la oficina y cómo obtener tu recibo.',
  },
  {
    titulo: 'Fugas y ahorro',
    texto: 'Aprende a detectar una fuga en casa, a quién reportarla y cómo cuidar el agua.',
  },
  {
    titulo: 'Trámites',
    texto: 'Nueva conexión, cambio de titular y reclamos por lecturas o cobros.',
  },
];

export default function Inicio() {
  return (
    <main className={estilos.pagina}>
      <header className={estilos.encabezado}>
        <p className={estilos.marca}>Oficina del Agua</p>
      </header>

      <section className={estilos.portada}>
        <h1 className={estilos.titulo}>Servicio de agua potable para tu comunidad</h1>
        <p className={estilos.descripcion}>
          ¿Tienes dudas sobre tu cobro, tus pagos o una fuga? Pregúntale a nuestro asistente virtual
          con el botón de la esquina inferior derecha.
        </p>
      </section>

      <section className={estilos.secciones}>
        {SECCIONES.map((seccion) => (
          <article key={seccion.titulo} className={estilos.tarjeta}>
            <h2>{seccion.titulo}</h2>
            <p>{seccion.texto}</p>
          </article>
        ))}
      </section>

      <footer className={estilos.pie}>
        Sistema de Gestión de la Oficina del Agua · Asistente virtual con RAG
      </footer>

      <ChatWidget />
    </main>
  );
}