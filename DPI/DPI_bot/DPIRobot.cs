using System;
using System.Collections.Generic;
using System.Drawing;
using Robocode;

namespace DPI_bot
{
    class DPIRobot:Robot
    {
        Random rand = new Random();
        public override void Run()
        {
            BodyColor = Color.Blue;
            var r = new Random();
            while (true)
            {
                TurnRadarRight(360);
            }
        }

        private bool EmergencyMode
        {
            get { return Energy < 20; }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            //TurnRight(evnt.Bearing);
            TurnGunRight(evnt.Bearing-GunBearing);
            if (GunHeat<0.1 && !EmergencyMode)
            {
                if (evnt.Distance < 300 && evnt.Velocity<0.5)
                {
                    Fire(Rules.MAX_BULLET_POWER);
                }
                else
                {
                    Fire(Rules.MIN_BULLET_POWER);
                }
            }
            TurnRight(rand.Next(0,360)-180);
            AheadNotIntoWall(150);
        }

        private double GunBearing
        {
            get
            {
                var res = GunHeading - Heading;
                return res;
            }
        }


        private void AheadNotIntoWall(double distance)
        {
            if ((Heading>0 && Heading<180 && X>BattleFieldWidth-distance)
            || (Heading > 90 && Heading < 270 && Y < distance)
            || (Heading > 180 && X < distance)
            || ((Heading > 270 || Heading < 90) && Y > BattleFieldHeight - distance))
            {
                TurnRight(180);
            }
            Ahead(distance);
        }
    }
}
