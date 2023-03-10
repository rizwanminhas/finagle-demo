import com.twitter.finagle.{Address, Http, ListeningServer, Name, Service, SimpleFilter}
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Duration, Future, JavaTimer, Timer}

object FinagleDemo {
  def main(args: Array[String]): Unit =
    (9090 to 9092).map { port =>
      simpleHttpServer(port)
    }.foreach(Await.ready)

  // GET localhost:9090?name=rizwan
  def stringLengthService = new Service[Request, Response]:
    override def apply(request: Request): Future[Response] =
      Future {
        val result = Option(request.getParam("name")).map(_.length).getOrElse(-1)
        val response = Response(status = Status.Ok)
        response.setContentString(result.toString)
        Thread.sleep(1200)
        response
      }

  def simpleHttpServer(port: Int): ListeningServer =
    Http.serve(s":$port", debugFilter(s"service-$port").andThen(stringLengthService))
}

object SimpleClient {
  def main(args: Array[String]): Unit =
    val originalClient: Service[Request, Response] = Http.newService("localhost:9090")
    val timeoutFilter = new TimeoutFilter[Request, Response](Duration.fromSeconds(1), new JavaTimer())
    val filteredClient = timeoutFilter.andThen(originalClient)
    val request = Request(Method.Get, "?name=rizwan")
    val response: Future[Response] = filteredClient(request)
    response.onSuccess(resp => println(resp.getContentString()))
    response.onFailure(exp => exp.printStackTrace())
    Thread.sleep(2000)
}

object LoadBalancedClient {
  def main(args: Array[String]): Unit =
    val addresses = (9090 to 9092).map(port => Address("localhost", port))
    val name: Name = Name.bound(addresses*)
    val client = Http.newService(name, "client")
    val requests = (1 to 20).map(i => Request(Method.Get, s"?name=${"rizwan" * i}"))
    val responses = requests.map(req => client(req).map(_.getContentString))
    Future.collect(responses).foreach(println)
    Thread.sleep(5000)
}

// filters aka middleware aka interceptors

/*

  ReqIn --> Filter --> RepOut --> Service --> ReqOut --> Filter --> RepIn

  Filter[ReqIn, RepOut, ReqOut, RepIn]
    apply(req: ReqIn, service: Service[RepOut, ReqOut]): Future[ReqOut]


  SimpleFilter[Req, Rep] == Filter[Req, Rep, Req, Req]
*/

class TimeoutFilter[Req, Rep](timeout: Duration, timer: Timer) extends SimpleFilter[Req, Rep] {
  override def apply(request: Req, service: Service[Req, Rep]): Future[Rep] =
    service(request).within(timer, timeout)
}

def debugFilter(id: String): SimpleFilter[Request, Response] = new SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    println(s"[$id] received reqiest $request")
    service(request)
  }
}

// loadbalancers