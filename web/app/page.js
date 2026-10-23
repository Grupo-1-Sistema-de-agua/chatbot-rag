import BarraSuperior from './_components/BarraSuperior';
import ChatAsistente from './_components/ChatAsistente';
import estilos from './page.module.css';

export default function Inicio() {
  return (
    <div className={estilos.pagina}>
      <div className={estilos.fondo} aria-hidden="true">
        <span className={estilos.mancha1} />
        <span className={estilos.mancha2} />
        <span className={estilos.mancha3} />
      </div>

      <BarraSuperior />

      <main className={estilos.principal}>
        <ChatAsistente />
      </main>

      <footer className={estilos.pie}>
        Sistema de Gestión de la Oficina del Agua · Asistente virtual con RAG
      </footer>
    </div>
  );
}