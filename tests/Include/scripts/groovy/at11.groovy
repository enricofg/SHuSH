import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import internal.GlobalVariable

public class at11 {

	@When("the confirmation window asks me if I want to store locally the information")
	def store_inf_local() {
	}

	@Then("the app stores the information")
	public void share_inf(){
		Mobile.tap(findTestObject('BtnMenu'), 0)
		Mobile.tap(findTestObject('BtnTabelas'), 0)
		Mobile.verifyElementNotExist(findTestObject('TextTabelaNoData'), 0)
	}
	@Given("I clicked the stop button")
	def I_click_share_button_random() {
		Mobile.startExistingApplication("com.example.shush")
		sleep(1500)
		Mobile.tap(findTestObject('BtnStart'), 0)
		sleep(1500)
		Mobile.tap(findTestObject('BtnStopRed'), 0)
	}
}
