import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.{Await, Future}

object FinagleDemo {
  def main(args: Array[String]): Unit =
    Await.ready(simpleHttpServer)

  // GET localhost:9090?name=rizwan
  def stringLengthService = new Service[Request, Response]:
    override def apply(request: Request): Future[Response] =
      Future {
        val input = request.getParam("name")
        val response = Response(status = Status.Ok)
        response.setContentString(input.length.toString)
        response
      }

  def simpleHttpServer: ListeningServer =
    Http.serve(":9090", stringLengthService)
}
