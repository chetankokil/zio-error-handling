package http

enum ErrorInfo extends Serializable:
  case InternalServerError(message: String)
  case BadRequest(message: String, errors: List[String] = List.empty)
  case NotFound(message: String)

