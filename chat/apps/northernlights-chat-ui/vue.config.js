module.exports = {
  devServer: {
    allowedHosts: "all",
    client: {
      // webSocketURL: 'wss://localhost/ws',
      webSocketURL: "auto://0.0.0.0:0/ws",
    },
  },
};
