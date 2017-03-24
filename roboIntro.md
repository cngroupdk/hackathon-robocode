<html>
<head>
  <style>
    .content {
      margin: auto;
      width:600px;
      font-size:18px;
      line-height: 1.5;
    }
    h1 {
      text-align: center;
      font-size: 64px;
      font-family: Consolas, monaco, monospace;
    }


  </style>
</head>
<body>
<div class="content">
<h1><span style="color: #CA224A">CN</span> Battle of Devs</h1>

<h2>Setup</h2>
Get Robocode <a href="http://robowiki.net/wiki/Robocode/Download">here</a>, then
setup your environment:
<ul>
  <li>
    .NET: </br>
    First you need <a href="https://sourceforge.net/projects/robocode/files/robocode/1.9.2.6/robocode.dotnet-1.9.2.6-setup.jar/download">
      Robocode .NET plugin
    </a></br>
    Afterwards follow these instructions:
    <a href="http://robowiki.net/wiki/Robocode/.NET/Create_a_.NET_robot_with_Visual_Studio">
      Create a .NET robot with Visual Studio
    </a>
  </li>
  <li>
    Java (Eclipse): </br>
    <a href="http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Project">Create a project</a></br>
    <a href="http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Robot">Create a robot</a></br>
  </li>
</ul>
<b>The namespace for your robot class has to have a unique name.</b> Ideally,
make it your team's name. This helps with identification when we run the bots.
It also helps to <b>choose a unique color for your robot</b>, we will agree on this
before we start coding.
<h2>

<h2>How do I win? (scoring)</h2>
The bot with most points wins, you get points for the following:</br>
<ul>
  <li>
    <b>50 points</b> for each robot alive when a bot is killed
  </li>
  <li>
    <b>10 points</b> for the last bot standing, for each enemy bot that died before it
  </li>
  <li>
    <b>1 point</b> for each 1 dmg you do to an enemy with a bullet. If you kill
     an enemy, you get 20% bonus for all damage you have inflicted on them.
  </li>
  <li>
    <b>2 points</b> for each 1 dmg you do to an enemy by ramming into them.
    If you kill an enemy, you get 30% bonus for all damage you have inflicted on them.
  </li>
</ul>

<h2>Robot anatomy</h2>
A robot consists of 3 parts:
<ul>
  <li>Body</li>
  <li>Gun</li>
  <li>Radar</li>
</ul>
The parts are aligned in 1 direction by default, but can move independently.
</br></br>
<img src="http://robowiki.net/w/images/3/30/Anatomy.jpg" alt="Robot anatomy"/>

<h3>Energy</h3>
<u>No energy, no fun.</u><br>
When bot's energy level reaches 0, the bot dies if depletion is caused by enemy,
otherwise the bot is disabled.</br>
<ul>
  <li>Energy is gained for hitting or ramming into your enemies.</li>
  <li>
    Energy is lost when hit by robot/bullet, when crashing into a wall, when firing
    bullets.
  </li>
</ul>
For more details, see <a href="http://robowiki.net/wiki/Robocode/Game_Physics">
Robocode - Game physics</a>

<h3>Controlling the radar</h3>
Usually makes up the smallest part of robot code, so don't design a military-grade
radar scanning bot :).
<ul>
  <li>At round start, the optimal direction is to scan the center of the field</li>
  <li>
    As the radar rotates, it creates an arc, every bot detected in the arc
   is sent to the onScannedRobot() method in order of distance from the scanning
   bot.
  </li>
  <li>Example strategies: same direction as gun, circular, tracking a specific bot</li>
</ul>
(More info at <a href="http://robowiki.net/wiki/Radar">Robowiki - Radar</a>)</br>

<h3>Controlling robot movement</h3>
<ul>
  <li>
    It is often very advantageous to not be the closest bot to any other bot, given
    that the closest scanned robot is the first one the scanner is notified of.
  </li>
  <li>Corners are safer</li>
  <li>Example strategies: stationary, straight, circular, back &amp; forth, random</li>
</ul>
(More info at <a href="http://robowiki.net/wiki/Movement">Robowiki - Movement</a>)

<h3>Targeting &amp; shooting</h3>
<ul>
  <li>
    You can choose bullet size when firing, larger bullets cause more damage,
    but travel slower, cost more energy to fire and cause longer cooldown between shots.
  </li>
  <li>Use larger bullets for short distance, smaller for larger distance</li>
  <li>
    Example strategies: shoot at current location of target, linear prediction of
    target position, circular prediction of target position, pattern match to find
    type of movement
  </li>
</ul>
(More info at <a href="http://robowiki.net/wiki/Targeting">Robowiki - Targeting</a>)

<h2>Programming</h2>
<h3>Event handlers</h3>
The basic event handlers you'll want to override in your robot class:
<ul>
  <li>
    <b>ScannedRobotEvent</b>.
     (<a href="http://robocode.sourceforge.net/docs/robocode/robocode/ScannedRobotEvent.html">Documentation</a>)
     Handle the ScannedRobotEvent by overriding the onScannedRobot() method;
     this method is called when the radar detects a robot.
  </li>
  <li>
    <b>HitByBulletEvent</b>.
    (<a href="http://robocode.sourceforge.net/docs/robocode/robocode/HitByBulletEvent.html">Documentation</a>)
    Handle the HitByBulletEvent by overriding the onHitByBullet() method;
    this method is called when the robot is hit by a bullet.
  </li>
  <li>
    <b>HitRobotEvent</b>.
    (<a href="http://robocode.sourceforge.net/docs/robocode/robocode/HitRobotEvent.html">Documentation</a>)
    Handle the HitRobotEvent by overriding the onHitRobot() method;
    this method is called when your robot hits another robot.
   </li>
  <li>
    <b>HitWallEvent</b>.
    (<a href="http://robocode.sourceforge.net/docs/robocode/robocode/HitWallEvent.html">Documentation</a>)
    Handle the HitWallEvent by overriding the onHitWall() method;
    this method is called when your robot hits a wall.
   </li>
</ul>

<h3>Rotation direction</h3>
<b>Robocode uses clockwise direction</b> convention </br></br>
<img src="http://www.ibm.com/developerworks/java/library/j-robocode2/fig2.gif"
alt="Robocode rotation direction convention"/>

<h3>Knowing your enemy</h3>
What you can find out about an enemy:
<ul>
  <li>Position</li>
  <li>Heading</li>
  <li>Velocity</li>
  <li>Angular velocity</li>
  <li>Energy</li>
  <li>
    How much damage you have done to them -- useful for comparing the other robots on
  	   the battlefield and targeting the one that you seem to be able to hit most (or least) easily
   </li>
  <li>How much damage they did to you</li>
  <li>How many times and when they have died</li>
  <li>When they fire a bullet (tricky technique using energy differential)</li>
</ul>

<h3>Documentation</h3>
You can find more in the API docs:
<ul>
  <li><a href="http://robocode.sourceforge.net/docs/robocode.dotnet/Index.html">.NET</a></li>
  <li><a href="http://robocode.sourceforge.net/docs/robocode/">Java</a></li>
</ul>
Useful information can also be found in the concise
<a href="http://robowiki.net/wiki/Robocode/FAQ">FAQ</a></br>
Robocode
<a href="http://robowiki.net/wiki/Robocode/Game_Physics">physics documentation</a>
</div>
</body>
</html>
