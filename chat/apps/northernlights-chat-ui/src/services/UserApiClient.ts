/* eslint-disable no-debugger */

import { config } from "@/services/EnvConfig";

class UserApiClient {
  private jwt = "";

  setJwt(jwt: string) {
    this.jwt = jwt;
  }

  async userInfo(): Promise<string> {
    console.log("UserApiClient::userinfo ...");
    return (
      (await (await UserApiClient.get(this.jwt, "info")).json()).chatterId || ""
    );
  }

  async subscribe(): Promise<string> {
    console.log("UserApiClient::subscribe ... ");
    return (await (await UserApiClient.post(this.jwt, "subscribe", {})).json())
      .chatterId;
  }

  private static async post(
    jwt: string,
    endpoint: string,
    payload: unknown
  ): Promise<Response> {
    return fetch(config.chatApiBaseUrl() + "/v1/user/api/" + endpoint, {
      method: "POST",
      body: JSON.stringify(payload), // string or object
      headers: new Headers({
        Authorization: "Bearer " + jwt,
        Accept: "application/json",
        "Content-Type": "application/json",
      }),
    });
  }

  private static async get(jwt: string, endpoint: string): Promise<Response> {
    return fetch(config.chatApiBaseUrl() + "/v1/user/api/" + endpoint, {
      method: "GET",
      headers: new Headers({
        Authorization: "Bearer " + jwt,
        Accept: "application/json",
      }),
    });
  }
}

const userApiClient = new UserApiClient();

export { userApiClient };
