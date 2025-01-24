package taehyeon.brothers.matchreal.presentation.interceptor

import jakarta.servlet.http.HttpServletRequest

object AuthorizationExtractor {
    private const val AUTHORIZATION = "Authorization"
    private const val BEARER_TYPE = "Bearer"
    const val ACCESS_TOKEN_TYPE = "AuthorizationExtractor.ACCESS_TOKEN_TYPE"

    fun extract(request: HttpServletRequest): String? {
        val headers = request.getHeaders(AUTHORIZATION)
        
        while (headers.hasMoreElements()) {
            val value = headers.nextElement()
            if (value.lowercase().startsWith(BEARER_TYPE.lowercase())) {
                val authHeaderValue = value.substring(BEARER_TYPE.length).trim()
                request.setAttribute(ACCESS_TOKEN_TYPE, value.substring(0, BEARER_TYPE.length).trim())
                
                val commaIndex = authHeaderValue.indexOf(',')
                return if (commaIndex > 0) {
                    authHeaderValue.substring(0, commaIndex)
                } else {
                    authHeaderValue
                }
            }
        }
        return null
    }
} 