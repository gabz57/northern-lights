/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_CHAT_API_SSE_BASE_URL: string;
  readonly VITE_CHAT_API_BASE_URL: string;
  readonly VITE_GOOGLE_CLIENT_ID: string;
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
