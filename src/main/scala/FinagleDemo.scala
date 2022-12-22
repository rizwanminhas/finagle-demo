import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Future}

object FinagleDemo {
  def main(args: Array[String]): Unit =
    Await.ready(simpleHttpServer)

  // GET localhost:9090?name=rizwan
  def stringLengthService = new Service[Request, Response]:
    override def apply(request: Request): Future[Response] =
      Future {
        val result = Option(request.getParam("name")).map(_.length).getOrElse(-1)
        val response = Response(status = Status.Ok)
        response.setContentString(result.toString)
        response
      }

  def simpleHttpServer: ListeningServer =
    Http.serve(":9090", stringLengthService)
}

object SimpleClient {
  def main(args: Array[String]): Unit =
    val client: Service[Request, Response] = Http.newService("localhost:9090")
    val request = Request(Method.Get, "/?name=rizwan")
    val response: Future[Response] = client(request)
    response.foreach(resp => println(resp.getContentString()))
    Thread.sleep(1000)
}