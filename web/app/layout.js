import { Roboto } from 'next/font/google';
import './globals.css';

const roboto = Roboto({
  subsets: ['latin'],
  weight: ['300', '400', '500', '700'],
  display: 'swap',
});

export const metadata = {
  title: 'Asistente virtual · Oficina del Agua',
  description: 'Asistente virtual de la oficina del agua: tarifas, cálculo del cobro, pagos, fugas y trámites.',
};

const scriptTema = `(function () {
  try {
    var tema = localStorage.getItem('tema');
    document.documentElement.setAttribute('data-tema', tema === 'oscuro' ? 'oscuro' : 'claro');
  } catch (e) {
    document.documentElement.setAttribute('data-tema', 'claro');
  }
})();`;

export default function RootLayout({ children }) {
  return (
    <html lang="es" data-tema="claro" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: scriptTema }} />
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.0.0/css/all.css" />
      </head>
      <body className={roboto.className}>{children}</body>
    </html>
  );
}