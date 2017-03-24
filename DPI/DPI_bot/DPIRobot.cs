using System;
using System.Collections.Generic;
using System.Drawing;
using System.Runtime.Remoting.Services;
using Robocode;

namespace DPI_bot
{
    class DPIRobot:Robot
    {
        private bool _isrunning = false;
        Random rand = new Random();
        public override void Run()
        {
            BodyColor = Color.Blue;
            ScanColor = Color.Blue;
            BulletColor = Color.White;
            GunColor = Color.Blue;
            RadarColor = Color.Blue;
            IsAdjustRadarForGunTurn = false;
            IsAdjustGunForRobotTurn = true;
            IsAdjustRadarForRobotTurn = false;

            var r = new Random();
            while (true)
            {
                TurnGunRight(90);
                TurnGunRight(90);
                TurnGunRight(90);
                TurnGunRight(90);
                Console.WriteLine("Basic Scan");
            }
        }

        private bool EmergencyMode
        {
            get { return Energy < 20; }
        }

        void TryFire(double distance)
        {
            Console.WriteLine("Firing");
            if (GunHeat < 0.1 && !EmergencyMode)
            {
                if (distance < 150)
                {
                    Fire(Rules.MAX_BULLET_POWER);
                }
                else if (distance <250)
                {
                    Fire(2);
                }
                else if (distance < 350)
                {
                    Fire(1);
                }
                else
                {
                    Fire(Rules.MIN_BULLET_POWER);
                }
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            Console.WriteLine("Scanned");

            Func<bool> isAimingAtMe =
                () =>
                {
                    var angleToMe = Math.Abs(Heading + evnt.Bearing - evnt.Heading);
                    return angleToMe < 200 && angleToMe > 160;
                };

            if (isAimingAtMe()&&evnt.Distance<250&&evnt.Velocity>1)
            {
                Console.WriteLine("Escaping ram");
                Dodge(evnt.Bearing);
            }
            else if (isAimingAtMe() || evnt.Velocity < 1 || evnt.Distance<250)
            {
                TurnGunRight(evnt.Bearing - GunBearing);
                Console.WriteLine("shooting slow target");
                TryFire(evnt.Distance);
            }
            else
            {
                Console.WriteLine("nothing to do");
                Dodge(rand.Next(0,355)-180);
            }
            Console.WriteLine("Scanned end");
            Scan();
        }



        public override void OnHitRobot(HitRobotEvent evnt)
        {
            Console.WriteLine("been hit by robot");
            Dodge(evnt.Bearing);
        }

        void TurnAndMove(double angle, double distance)
        {
            Console.WriteLine("TURN AND MOVE angle: " + angle);
            if (angle > 180)
            {
                TurnRight(angle - 180);
                Ahead(-distance);
            }
            else if (angle < -180)
            {
                TurnRight(angle + 180);
                Ahead(-distance);
            }
            else
            {
                TurnRight(angle);
                Ahead(distance);
            }
        }

        private void Dodge(double bearing)
        {
            TurnAndMove(bearing - 90, rand.Next(0, 1) == 0 ? 250 : -250);
        }

        public override void OnHitByBullet(HitByBulletEvent evnt)
        {
            Console.WriteLine("been hit by bullet");
            Dodge(evnt.Bearing);
        }

        private double RadarBearing
        {
            get
            {
                var res = RadarHeading - Heading;
                return res;
            }
        }

        private double GunBearing
        {
            get
            {
                var res = GunHeading - Heading;
                return res;
            }
        }
    }
}
