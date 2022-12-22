### To test the `simpleHttpServer`:
1. Run the `main` in `FinagleDemo.scala` with the `simpleHttpServer`.
2. Open a terminal and type `curl "http://localhost:9090?name=rizwan"` and it should return you `6`. Use the `-v` flage for verbose i.e. more details:
   ```
   curl "http://localhost:9090?name=rizwan" -v                                                                                                                    ─╯
    *   Trying 127.0.0.1:9090...
    * Connected to localhost (127.0.0.1) port 9090 (#0)
    > GET /?name=rizwan HTTP/1.1
    > Host: localhost:9090
    > User-Agent: curl/7.79.1
    > Accept: */*
    >
    * Mark bundle as not supporting multiuse
      < HTTP/1.1 200 OK
      < Content-Length: 1
      <
    * Connection #0 to host localhost left intact
      6%
    ```
   **Note:** I had to use quotes in the curl command because of `?`. Another option is to escape `?` with `\` i.e. `curl http://localhost:9090\?name=rizwan -v`