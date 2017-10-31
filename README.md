<p>Spring boot backed javafx trading platform that unifies highly heterogeneous data sources over the web so user does not have to pay for any data.</p>
<p>This is a highly moduler and loosely coupled application with layered architecture, each vertical slice of which can work fully independently. The code is written using several of design patterns and and keeping SOLID, GRASP, KISS, DRY, YAGNI principles in mind.</p>

<b>Technology Stack:</b><br><br>

<b>Build tool:</b> Maven<br>
<b>IOC Container:</b> Spring boot - auto-configuration, async, scheduling, i18n, caching, data<br>
<b>Cache Provider:</b> ehcache<br>
<b>ORM Provider:</b> Hibernate<br>
<b>Bean Validation:</b> Hibernate validator<br>
<b>Code Generation:</b> Lombok<br><br>

<h3>Compoent Diagram</h3><br><br>

![Component Diagram](https://github.com/paragparalikar/stox/blob/master/stox-component-diaram.jpg)

<br>
<p><b>Core:</b> Core module hosts common models, interfaces as well as utility classes</p>
<p><b>Data:</b> Data module deals with persistence of instrument master data, trading time series data and user data. This layer is also responsible for Master Data Management and data integrity</p>
<p><b>Workbench:</b> This is desktop application and provides ui infrastructure for other modules</p>
<p><b>Instrument Navigation:</b> Module that implements use case for user can navigate through various instruments available for trading</p>
<p><b>Chart:</b> Interactive stock charts re-written from zero, as javafx charting library was too slow for rendering thousands of data points in sub-second timeframe</p>
<p><b>Indicators:</b> Modules that hosts various in-built indicators</p>
<p><b>Screening:</b> Modules that hosts various in-built screeners</p>
<p><b>Portfolio Management:</b> This module is yet to be developed</p>
<p><b>Reporting:</b> This module is yet to be developed</p>
<p><b>Alerts and Notifications:</b> This module is yet to be developed</p>
<p><b>Trading:</b> This module is yet to be developed</p>



<h3>Layered Architecute</h3><br><br><br><br>

![Layered architecture](https://github.com/paragparalikar/stox/blob/master/layered-architecture.png)

<h3>Screenshots</h3>

Multiwindow docking layout

![Bootstrap dialog](https://github.com/paragparalikar/stox/blob/master/screenshot-multiwindow.PNG)

Various built in Indicators

![Bootstrap form](https://github.com/paragparalikar/stox/blob/master/screenshot-indicators.PNG)

Bootstrap css 

![Bootstrap dialog](https://github.com/paragparalikar/stox/blob/master/screenshot-bootstrap-dialog.PNG)

![Bootstrap form](https://github.com/paragparalikar/stox/blob/master/screenshot-bootstrap-form.PNG)


