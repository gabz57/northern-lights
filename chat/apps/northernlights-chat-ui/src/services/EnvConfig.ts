class EnvConfig {

    // note: on vite, prefix for env variable is VITE_
    // they can be imported with import.meta.env.VITE_API_ENDPOINT

    public chatApiSseBaseUrl(): string {
        return process.env.VUE_APP_CHAT_API_SSE_BASE_URL as string
    }
    public chatApiBaseUrl(): string {
        return process.env.VUE_APP_CHAT_API_BASE_URL as string
    }
}
const config = new EnvConfig();

export {config};
