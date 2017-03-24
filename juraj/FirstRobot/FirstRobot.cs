using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;

using Robocode;

namespace Juraj 
{
    public class PowerPuffPanda : Robot
    {
        public override void Run()
        {
            SetAllColors(System.Drawing.Color.Purple);
            TurnLeft(Heading - 90);
            TurnGunRight(90);
            
            while(true)
            {
                Ahead(500);

                TurnRight(90);
            }
        }

        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }

        public override void OnHitByBullet(HitByBulletEvent evnt)
        {
            base.OnHitByBullet(evnt);
        }
    }
}
