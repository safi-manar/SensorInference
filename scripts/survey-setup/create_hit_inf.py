#!/usr/bin/env python3

import sys

import mturk

question = '''<?xml version="1.0"?>
<HTMLQuestion xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2011-11-11/HTMLQuestion.xsd">
    <HTMLContent><![CDATA[
        <!DOCTYPE html>
        <html>
            <head>
                <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
                <script type='text/javascript' src='https://s3.amazonaws.com/mturk-public/externalHIT_v1.js'></script>
            </head>
            <body>
                <!-- For help on using this template, see the blog post: http://mechanicalturk.typepad.com/blog/2014/04/editing-the-survey-link-project-template-in-the-ui.html --><!-- Bootstrap v3.0.3 -->
                <link href="https://s3.amazonaws.com/mturk-public/bs30/css/bootstrap.min.css" rel="stylesheet" />
                <section class="container" id="SurveyLink" style="margin-bottom: 15px; padding: 10px; font-family: Verdana, Geneva, sans-serif; color: rgb(51, 51, 51);">
                <div class="row col-xs-12 col-md-12"><!-- Instructions -->
                <div class="panel panel-primary">
                <div class="panel-heading" style="font-size: 0.9em;"><strong>Instructions</strong></div>

                <div class="panel-body">
                <p style="font-size: 0.9em;"><strong>In order for you to understand your participation in this study, please read this&#160;entire disclosure.</strong></p>

                <p style="font-size: 0.9em;">&#160;</p>

                <p style="font-size: 0.9em;">This app is part of a study to explore the possibility of inferring user information from benign smartphone sensors, like the step counter or compass.</p>

                <p style="font-size: 0.9em;">This app will silently collect periodic sensor measurements.It will also collect GPS data.&#160;<br />
                All collected data will be kept on our secure servers. The data will be maintained for possible future research. Only anonymous, non-identifiable data (ie, not GPS) would be shared with other researchers. Data collection will have minimal impact on your phone performance and battery life.Your participation in this study will involve 1 required survey in addition to brief daily optional surveys.</p>

                <p style="font-size: 0.9em;">&#160;</p>

                <p style="font-style: italic; padding: 10px; background-color: rgb(238, 238, 238);"><span style="font-size: 11.7px;"><b>For Amazon MTURK participants:<br />
                <br />
                - $0.25 for downloading and installing app<br />
                - $1 for each daily survey you submit<br />
                - $2 for 1 week complete participation in the study</b></span></p>

                <p style="font-size: 0.9em;"><strong>Sensors Inference Project Team</strong><br />
                sensor-inference@icsi.berkeley.edu<br />
                Computer Science Department, UC Berkeley<br />
                International Computer Science Institute</p>

                <p style="font-size: 0.9em;">&#160;</p>

                <p style="font-size: 0.9em;"></p>

                <p style="font-size: 0.9em; font-style: italic; padding: 10px; background-color: rgb(238, 238, 238);"><strong style="font-family: Verdana, Geneva, sans-serif; font-size: 11.7px; line-height: 20.8px;">**IMPORTANT** &#160; &#160; Please keep this window open as you install the app. When you install the app, you will be given a verification code that you must paste in the box below. <u>THE VERIFICATION CODE&#160;IS THE ONLY WAY THAT WE CAN VERIFY AND PAY YOU.</u></strong></p>

                <p style="font-size: 11.7px; font-family: Verdana, Geneva, sans-serif; line-height: 20.8px;">&#160;</p>

                <center style="font-size: 0.9em;"><img src="http://i64.tinypic.com/2l1jrn.png" style="width:336px;height:600px;" /></center>

                <p style="font-size: 11.7px; font-family: Verdana, Geneva, sans-serif; line-height: 20.8px;">&#160;</p>
                </div>
                </div>
                <!-- End Instruction -->

                <table style="font-size: 0.9em;">
                <tbody>
                <tr>
                <td><strong>APP Playstore link:</strong></td>
                <td><a href="GitHub: https://github.com/safi-manar/SensorInference (Use GooglePlay)" target="_blank">https://github.com/safi-manar/SensorInference</a></td>
                </tr>
                <tr>
                <td><strong>Enter your MTURK verification code here:</strong></td>
                <td style="vertical-align: middle;"><input class="form-control" id="surveycode" name="surveycode" placeholder="e.g. 12a34b56" type="text" /></td>
                </tr>
                </tbody>
                </table>
                </div>
                </section>
                <!-- close container section -->
                <style type="text/css">td {
                    font-size:1.0em;
                padding:5px 5px;
                }
                </style>
            </body> </html>
    ]]>
    </HTMLContent>
    <FrameHeight>450</FrameHeight>
</HTMLQuestion>
'''

options = {
    'Title': 'UC Berkeley Sensor Study',
    'Description': 'Download our Android app, fill out a survey, and let the app run for a week ( + $2.00 bonus) .',
    'Keywords': 'Android, app, download, install, survey, bonus',

    'Reward': [{
        'Amount': 1.00,
        'CurrencyCode': 'USD'
    }],
    'MaxAssignments': 1,
    'AssignmentDurationInSeconds': 60*60,
    'LifetimeInSeconds': 60*60*24*7,
    'AutoApprovalDelayInSeconds' : 60*60*24*10,

#   'QualificationRequirement': [{
#       'QualificationTypeId': qualification_id,
#       'Comparator': 'Exists',
#       'RequiredToPreview': 'true'
#   }, {
#       'QualificationTypeId': mturk.LOCALE,
#       'Comparator': 'EqualTo',
#       'LocaleValue': {
#           'Country': 'US'
#       },
#       'RequiredToPreview': 'true'
#   }, {
#       'QualificationTypeId': mturk.P_APPROVED,
#       'Comparator': 'GreaterThan',
#       'IntegerValue': 95,
#       'RequiredToPreview': 'true'
#   }],

    'Question': question
}

m = mturk.MechanicalTurk()
r = m.request('CreateHIT', options)
print(r)
