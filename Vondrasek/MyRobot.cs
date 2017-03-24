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
            if (ShotIt(e))
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
            }
            else
            {
                TurnRight(e.Bearing);
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
            if (e.Bearing < 5 && e.Bearing > -5)
            {
                Fire(3);
                Ahead(40);
            }
            else if (e.Bearing < 45 && e.Bearing > -45)
            {
                TurnRight(e.Bearing);
                Fire(3);
                Ahead(40);
            }
            else
            {
                Back(20);
            }              

            base.OnHitRobot(e);
        }

        bool ShotIt(ScannedRobotEvent e)
        {
           

            if (e.Bearing < 45 && e.Bearing > -45)
            {
                if (GoingTogether(e))
                    return true;

                if (e.Velocity < 6)
                    return true;              
            }
            return false;
        }

        bool GoingTogether(ScannedRobotEvent e)
        {
            if (e.Heading - Heading < 30 && e.Heading - Heading > -30)
                return true;
            if (e.Heading - Heading < 180 && e.Heading - Heading > 150)
                return true;
            if (e.Heading - Heading < -150 && e.Heading - Heading > -180)
                return true;
            return false;
        }
    }
}
