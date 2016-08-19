#!/usr/bin/env python3

import mturk

question = '''<?xml version="1.0"?>
<QuestionForm xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionForm.xsd">
    <Overview>
        <FormattedContent><![CDATA[
            <p><img alt="App logo" src="https://blues.cs.berkeley.edu/sensor-inference/images/app-logo.png" /> UC Berkeley Sensor Study</p>

            <p>Project site: <a href = "https://blues.cs.berkeley.edu/sensor-inference/">https://blues.cs.berkeley.edu/sensor-inference/</a></p>

            <p>This is a week-long data collection task that&#39;s part of a study to identify relationships between user information and smartphone sensor measurements. Participants will install an app on their Android smartphone for this HIT, and can respond to optional periodic surveys over the course of the week for bonuses.</p>

            <p>Payments are made upon completion of surveys:</p>

            <ul>
            <li>$1.00 for the entry demographic survey</li>
            <li>$1.00 bonus for an optional daily activity survey (presented at around 8:00 PM each night, max 6 nights)</li>
            <li>$2.00 bonus for an optional exit survey</li>
            </ul>

            <p>Our app requires the following permissions:</p>

            <ul>
            <li>Location, used with survey responses to test our models and predictions; <strong><em>GPS data will be kept confidential and never shared outside our research group</em></strong></li>
            </ul>

            <p>&#160;</p>

            <p><u>How to install the app and participate in the study:</u></p>

            <ol>
            <li>From a compatible Android phone, point your browser to <strong><em><a href = "http://tinyurl.com/ucbsensormon">http://tinyurl.com/ucbsensormon</a></em></strong>. This is a short link to our Google Play Store listing at <a href = "https://play.google.com/store/apps/details?id=edu.berkeley.icsi.sensormonitor">https://play.google.com/store/apps/details?id=edu.berkeley.icsi.sensormonitor</a> </li>
            <li>Install our Sensor Monitor app through the Google Play Store. Unfortunately, we're unable to support all phone models, so you may not be able to participate if our software is unavailable for your device.</li>
            <li>In the app, review our consent form, grant the appropriate permissions, and complete the entry survey.</li>
            <li>Enter your verification code in the box below. <strong><em>Accurate entry is necessary to ensure assignment approvals and payments.</em></strong></li>
            <li>Optional: complete daily surveys for bonus payments.</li>
            <li>The app will present an exit survey and prompt you to uninstall it at the end of the study period.</li>
            </ol>
        ]]></FormattedContent>
    </Overview>
    <Question>
        <QuestionIdentifier>surveycode</QuestionIdentifier>
        <DisplayName>App Code</DisplayName>
        <IsRequired>true</IsRequired>

        <QuestionContent><Text>Enter the app-generated verification code here (shown after completing the in-app survey):</Text></QuestionContent>
        <AnswerSpecification>
            <FreeTextAnswer>
                <Constraints><Length minLength="8" maxLength="8" /></Constraints>
                <NumberOfLinesSuggestion>1</NumberOfLinesSuggestion>
            </FreeTextAnswer>
        </AnswerSpecification>
    </Question>
</QuestionForm>
'''

options = {
    'Title': 'UC Berkeley Sensor Inference Study',
    'Description': 'Download our Android app, fill out a survey, and let the app run for a week (+ up to $8.00 in bonuses).',
    'Keywords': 'Android, app, download, install, survey, bonus',

    'Reward': [{
        'Amount': 1.00,
        'CurrencyCode': 'USD'
    }],
    'MaxAssignments': 200,
    'AssignmentDurationInSeconds': 60*60,
    'LifetimeInSeconds': 60*60*24*7,
    'AutoApprovalDelayInSeconds' : 60*60*24*10,

    'QualificationRequirement': [
    {
        'QualificationTypeId': mturk.LOCALE,
        'Comparator': 'EqualTo',
        'LocaleValue': {
            'Country': 'US'
        },
        'RequiredToPreview': 'true'
    }
#    ,{
#       'QualificationTypeId': mturk.P_APPROVED,
#       'Comparator': 'GreaterThan',
#       'IntegerValue': 95,
#       'RequiredToPreview': 'true'
#    }
    ],

    'Question': question
}

m = mturk.MechanicalTurk()
r = m.request('CreateHIT', options)
print(r)
