(ns ic.demo)
(require '[reitit.ring :as ring])
(require '[reitit.http :as http])
(require '[reitit.interceptor.sieppari :as sieppari])

(defn interceptor [number]
  (println "in interceptor")
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

(defn demo-middleware [handler]
  (fn [request]
   (println "in demo-middleware")
    (handler request)))

(def router-config {:data {:middleware [demo-middleware]}})

(def app
  (http/ring-handler
   (http/router
    ["/api"
     {:interceptors [(interceptor 1)]}

     ["/number"
      {:interceptors [(interceptor 10)]
       :get {:interceptors [(interceptor 100)]
             :handler (fn [req]
                        (println "in handler")
                        {:status 200
                         :body (select-keys req [:number])})}}]])

   ;; the default handler
   (ring/create-default-handler)

   ;; executor
   {:executor sieppari/executor}))


;(app {:request-method :get, :uri "/"})
; {:status 404, :body "", :headers {}}

;(app {:request-method :get, :uri "/api/number"})
; {:status 200, :body {:number 111}}
