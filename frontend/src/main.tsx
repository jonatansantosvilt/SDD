import ReactDOM from 'react-dom/client';
import { createGlobalStyle, ThemeProvider } from 'styled-components';
import { theme } from './theme/theme';
import { StatusPage } from './pages/StatusPage';

const GlobalStyle = createGlobalStyle`
  * { box-sizing: border-box; }
  html, body { margin: 0; padding: 0; }
  body {
    background: ${({ theme }) => theme.colors.bg};
    color: ${({ theme }) => theme.colors.text};
    font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
    line-height: 1.4;
  }
`;

// Note: StrictMode is intentionally omitted so effects (initial fetch + the auto-refresh
// interval) run exactly once, giving deterministic refresh behavior in the app and in tests.
ReactDOM.createRoot(document.getElementById('root')!).render(
  <ThemeProvider theme={theme}>
    <GlobalStyle />
    <StatusPage />
  </ThemeProvider>,
);
