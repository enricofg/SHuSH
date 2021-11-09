Feature: Check Dashboard
As a user I want to open the application
So i can record the noise level in my area


Scenario: Check Dashboard
					Given I have opened the application
					When I check the dashboard
					Then the label "DB" is set to "70"
					And the label "Nivel" is set to "Nível"
					And the label "Maximo" is set to "xx"
					And the label "Minimo" is set to "yy"
					And the label "Mediana" is set to "zz"
					And the ON/OFF radio button exists
					Then I close the application
					
					
					
