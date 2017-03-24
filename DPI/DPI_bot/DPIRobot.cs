using System;
using System.Collections.Generic;
using Robocode;

namespace DPI_bot
{
    class DPIRobot:Robot
    {
        public override void Run()
        {
            while (true)
            {
                if (EmergencyMode)
                {
                    
                }
                else
                {
                    
                }
            }
        }

        private bool EmergencyMode
        {
            get { return Energy < 20; }
        }

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            if (!EmergencyMode)
            {
                GunHeading
            }
        }
    }
}
