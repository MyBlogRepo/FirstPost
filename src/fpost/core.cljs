(ns ^:figwheel-hooks fpost.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]))

(println "--------------ver 16--------------") ;logging

(rf/reg-event-db
  :initialise-db				 ;; usage: (rf/dispatch [:initialise-db])
  (fn [_ val]					 ;; Ignore first param (db )
		(println "DB-Init: " val)
	 {:seen {:plane false :bird false}}))

(rf/reg-sub 
   :is-it
   (fn [db [_ what]] 
	  (println "Subscription :seen")
	  (get-in db [:seen what])))

(rf/reg-event-db
  :toggle	
  (fn [db [_ what]]
	(println (str "Event db :toggle " what))
	(update-in db [:seen what] not)))
	 
(defn see-button [description message]
  [:button {:type "button" :class "btn-primary"
			:on-click (fn [e]
                 (.preventDefault e)
                 (rf/dispatch [:toggle message]))}
    description])

(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
  (println "Render Hello World")
  (let[plane? @(rf/subscribe [:is-it :plane])
       bird? @(rf/subscribe [:is-it :bird])
	   super? (and plane? bird?)]
  [:div
   [:h1 "Welcome to The Simple Example"]   
   (see-button "Is it a plane?" :plane)
   (see-button "Is it a bird?" :bird)
   [:h3 (if plane? "It is a Plane")]
   [:h3 (if bird? "It is a Bird")]
   [:h1 (if super? "It's a SuperTrain")]
  ]))

(defn mount [el]
  (rdom/render [hello-world] el))
 
(defn mount-app-element []
(rf/dispatch-sync [:initialise-db 10])
  (when-let [el (get-app-element)]     
	(mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
