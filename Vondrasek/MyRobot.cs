using Robocode;
using System;
using System.Drawing;

namespace Vondrasek
{
    public class RamVondrasek : Robot
    {


        /// <summary>
        /// Run It
        /// </summary>
        public override void Run()
        {
            BodyColor = Color.PapayaWhip;

            while (true)
            {          
                TurnRadarRight(360);
            }
        }
                

        /// <summary>
        /// Robot Scanned
        /// </summary>
        /// <param name="e"></param>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {            
            TurnRight(e.Bearing);  
          
            

            if (e.Distance < 300)
            {
                if (e.Distance < 150)
                {
                    if (Energy > 20)
                        Fire(3);
                    else
                        Fire(1);
                }
                else
                    Fire(1);
            }    
            Ahead(100);
        }

        /// <summary>
        /// Billet Hit
        /// </summary>
        /// <param name="e"></param>
        public override void OnHitByBullet(HitByBulletEvent e)
        {
            // Replace the next line with any behavior you would like
        }

        /// <summary>
        /// Wall Hit
        /// </summary>
        /// <param name="e"></param>
        public override void OnHitWall(HitWallEvent e)
        {
            // Replace the next line with any behavior you would like
        }

        /// <summary>
        /// Ram that shit
        /// </summary>
        /// <param name="evnt"></param>
        public override void OnHitRobot(HitRobotEvent e)
        {


            TurnRight(e.Bearing);

            if (Energy > 20)
                Fire(3);
            else
                Fire(1);

            Back(2);
            Ahead(40);

            base.OnHitRobot(e);
        }
    }
}
