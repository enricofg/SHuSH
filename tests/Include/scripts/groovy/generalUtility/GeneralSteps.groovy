package generalUtility
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.connection.ConnectionState
import io.appium.java_client.android.connection.ConnectionStateBuilder

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When



class GeneralSteps {


	@Given("I have opened the application")
	def open_app() {

		Mobile.startExistingApplication("com.example.shush")
	}

	@Given("I have started the audio detection")
	def open_app_detect_sound() {

		Mobile.startExistingApplication("com.example.shush")
		Mobile.tap(findTestObject('BtnStart'), 0)
	}
	@Given("I have clicked the menu button")
	def clicked_menu() {

		Mobile.startExistingApplication("com.example.shush")
		Mobile.tap(findTestObject('BtnMenu'), 0)
	}

	@Given("I have click the share button")
	def click_shar() {
		Mobile.startExistingApplication("com.example.shush")
		Mobile.tap(findTestObject('BtnStart'), 0)
		Mobile.tap(findTestObject('share'), 0)
	}

	@Given("the sound detection is turned off")
	def turn_off_detection() {
		Mobile.startExistingApplication("com.example.shush")
		Mobile.tap(findTestObject('BtnStart'), 0)
		sleep(2000)
		Mobile.tap(findTestObject('BtnStopRed'), 0)
	}


	@Given("my internet data is turned off")
	def turn_internet_off() {
		Mobile.startExistingApplication("com.example.shush")
		AppiumDriver<?> driver = MobileDriverFactory.getDriver()
		ConnectionState cs = new ConnectionStateBuilder().withWiFiDisabled().build()
		driver.setConnection(cs)
		Mobile.tap(findTestObject('BtnStart'), 0)
	}

	@When("I check the dashboard")
	def check_dashboard() {
	}

	@When("I click the stop button")
	def stop_btn() {
		Mobile.tap(findTestObject('BtnStop'), 0)
	}

	@When("I click the pause button")
	def pause_btn() {
		Mobile.tap(findTestObject('BtnPause'), 0)
	}

	@When("I click the play button")
	def play_btn() {
		Mobile.tap(findTestObject('BtnStart'), 0)
	}

	@When("I start the sound detection")
	def play_sound_detection() {
		Mobile.tap(findTestObject('BtnStart'), 0)
	}

	@When("I pause the sound detection")
	def pause_sound_detection() {
		Mobile.tap(findTestObject('BtnPause'), 0)
	}

	@When("I clicked the menu button")
	def menu_btn() {
		Mobile.tap(findTestObject('BtnMenu'), 0)
	}

	@When("I clicked the Graphics navigation link")
	def graphics_btn() {
		Mobile.tap(findTestObject('BtnGrafico'), 0)
	}

	@When("I clicked the Map navigation link")
	def maps_btn() {
		Mobile.tap(findTestObject('BtnMapas'), 0)
	}

	@When("I haved click the share button")
	def click() {
		Mobile.tap(findTestObject('share'), 0)
	}

	@When("my sound detection is turned off")
	def dound_detection_turned_off() {
		Mobile.tap(findTestObject('BtnStopRed'), 0)
	}

	@Then("my location services is turned off")
	def location_off() {
		Mobile.getText(findTestObject('LocationOff'), 0)
	}



	@Given("I have waited a few seconds")
	def wait_tim() {
		sleep(3000)
	}



	@Then("I verify that the {string} label value stops changing")
	def verify_label_value_dont_change(String label) {

		String value = Mobile.getAttribute(findTestObject("Valor"+label), "text", 0)
		sleep(3000)
		String value2 = Mobile.getAttribute(findTestObject("Valor"+label), "text", 0)
		Mobile.verifyEqual(value, value2)
	}

	@Then("the label {string} is set to {string}")
	def verify_label_value(String label,String value) {
		Mobile.verifyElementText(findTestObject('Valor'+label), value)
	}

	@Then("I verify that the {string} label is {string}")
	def I_verify_label_value(String label,String value) {
		Mobile.verifyElementText(findTestObject('Valor'+label), value)
	}


	@Then("I verify that the {string} label value is changing")
	def verify_label_value_change(String label) {

		String value = Mobile.getAttribute(findTestObject("Valor"+label), "text", 0)
		sleep(3000)
		String value2 = Mobile.getAttribute(findTestObject("Valor"+label), "text", 0)
		Mobile.verifyNotEqual(value , value2)
	}


	@Then("Verify that the Nível label value is changing")
	def verify_nivel_value_change() {

		String value = Mobile.getAttribute(findTestObject('ValorNivel'), "text", 0)
		sleep(3000)
		String value2 = Mobile.getAttribute(findTestObject('ValorNivel'), "text", 0)
		Mobile.verifyNotEqual(value , value2)
	}



	@Then("I verify that the circle background color is changing")
	def color_background_change(){
		String value= Mobile.getAttribute(findTestObject('ValorNivel'),'text' , 0)
		sleep(3000)
		String value2 = Mobile.getAttribute(findTestObject("ValorNivel"), "text", 0)
		Mobile.verifyNotEqual(value , value2)
	}

	@Then("the ON\\/OFF radio button exists")
	def verify_radio_exists(){
		Mobile.verifyElementExist(findTestObject("Switch"), 0)
	}
	@Then("I close the application")
	public void close_app() {
		Mobile.closeApplication()
	}

	@Then("I verify that the {string} label value starts changing again")
	def verify_label_value_again(String label) {

		String value = Mobile.getAttribute(findTestObject("Valor"+label), "text", 0)
	}

	@Then("the stop button is grayed out")
	def stop_btn_grayed() {
		Mobile.verifyElementExist(findTestObject("BtnStop"), 0)
	}

	@Then("the play button is grayed out")
	def play_btn_grayed() {
		Mobile.verifyElementExist(findTestObject("BtnStartGray"), 0)
	}

	@Then("the pause button is grayed out")
	def pause_btn_grayed() {
		Mobile.verifyElementExist(findTestObject("BtnStop"), 0)
	}

	@Then("the stop button turns to red")
	def stop_btn_red() {
		Mobile.verifyElementExist(findTestObject("BtnStopRed"), 0)
	}

	@Then("the pause button turns to blue")
	def pause_btn_blue() {
		Mobile.verifyElementExist(findTestObject("BtnPauseBlue"), 0)
	}

	@Then("the play button turns to green")
	def play_btn_green() {
		Mobile.verifyElementExist(findTestObject("BtnStart"), 0)
	}

	@Then("the menu shows the available navigation pages")
	def menu_navigation() {
		Mobile.verifyElementExist(findTestObject("BtnDashboard"), 0)
		Mobile.verifyElementExist(findTestObject("BtnMapas"), 0)
		Mobile.verifyElementExist(findTestObject("BtnGrafico"), 0)
	}

	@Then("the app redirects me to the graphics of past recording data screen")
	def redirects_graphics_page() {
		Mobile.verifyElementExist(findTestObject("graficos_texto"), 0)
	}

	@Then("the app redirects me to the shared noise data screen")
	def redirects_map_page() {
		Mobile.verifyElementExist(findTestObject("mapa_texto"), 0)
	}

	@Then("the app asks me to turn my location services on")
	def location_turn_on() {
		Mobile.getText(findTestObject('LocationTurnOn'),0)
	}

	@Then("the app warn me I have no data")
	def no_internet() {
		Mobile.getText(findTestObject('NoInternet'),0)
	}

	@Then("I cannot share the noise detection information")
	def cant_share() {
		Mobile.getText(findTestObject('CantShareWithoutInternet'),0)
	}

	@Then("the default db level is '-'")
	def defaultDB() {
		Mobile.verifyElementExist(findTestObject('DefaultDB'),0)
	}

	@Then("the app warns me the sound detection is stopped")
	def warning_sound_detection_stopped() {
		Mobile.verifyElementExist(findTestObject('StoppedSoundDetectionMessage'),0)
	}
}