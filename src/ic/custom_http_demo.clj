(ns ic.custom-http-demo)

(require '[reitit.ring :as ring])
(require '[reitit.http :as http])

(require '[reitit.interceptor.sieppari :as sieppari])

(defn before-fn [request]
  (println "before" request))

(defn after-fn []
  (println "after"))

(defn interceptor [number]
  (println "in interceptor")
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

(defn response-handler [response]
  (println response)
  response)

(defn failure-handler [error]
  (println error)
  error)

(defn my-http-handler
  ([router]
   (my-http-handler router nil))
  ([router default-handler]
   (my-http-handler router default-handler nil))
  ([router default-handler opts]
     (with-meta
      (fn
        ([request]
         (println "in single request handler")
         (println opts)
         (before-fn request)
         (let [handler (http/ring-handler router default-handler opts)
               response (handler request)]
           (after-fn)
           response))
        ([request respond raise]
         (println "in request respond raise handler")
         (before-fn request)
         (let [handler (http/ring-handler router default-handler opts)
               response (handler request respond raise)]
           (after-fn)
           (println response)
           {:status 202})))
      {::http/router router})))

(def app
  (my-http-handler
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
