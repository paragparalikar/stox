<p>A JavaFX UI driven by Spring boot for highly complex interactive stock charts which utilise free data sources.</p>
<p>This is a highly moduler and loosely coupled application with layered architecture, each vertical slice of which can work fully independently. The code is written using several of design patterns and good coding practices and in SPI style.</p>

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
<p><b>Data:</b> Data module deals with persistence of instrument master data, trading time seriese data and user data. This layer is also responsible for Master Data Management and data integrity</p>
<p><b>Workbench:</b> This is desktop application and provides ui infrastructure for other modules</p>



<h3>Layered Architecute</h3><br><br>><br><br>
![Layered architecture](https://github.com/paragparalikar/stox/blob/master/layered-architecture.png)
