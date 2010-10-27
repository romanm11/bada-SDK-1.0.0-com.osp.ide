/**
 * Name        : $(baseName)
 * Version     : $(version)
 * Vendor      : $(vendor)
 * Description : $(description)
 */

#include "$(baseName).h"

using namespace Osp::App;
using namespace Osp::Base;
using namespace Osp::System;
using namespace Osp::Graphics;

$(baseName)::$(baseName)()
{
}

$(baseName)::~$(baseName)()
{
}

Application*
$(baseName)::CreateInstance(void)
{
	// Create the instance through the constructor.
	return new $(baseName)();
}

bool
$(baseName)::OnAppInitializing(AppRegistry& appRegistry)
{
	// TODO:
	// Initialize UI resources and application specific data.
	// The application's permanent data and context can be obtained from the appRegistry.
	//
	// If this method is successful, return true; otherwise, return false.
	// If this method returns false, the application will be terminated.

	// Uncomment the following statement to listen to the screen on/off events.
	//PowerManager::SetScreenEventListener(*this);
	return true;
}

bool
$(baseName)::OnAppTerminating(AppRegistry& appRegistry, bool forcedTermination)
{
	// TODO:
	// Deallocate resources allocated by this application for termination.
	// The application's permanent data and context can be saved via appRegistry.
	return true;
}

void
$(baseName)::OnForeground(void)
{
	// TODO:
	// Start or resume drawing when the application is moved to the foreground.

	Canvas* pCanvas = GetAppFrame()->GetCanvasN();

	Font font;
	font.Construct(FONT_STYLE_PLAIN | FONT_STYLE_BOLD, 50);
	pCanvas->SetFont(font);
	pCanvas->DrawText(Point(30, 30), GetAppName());
	pCanvas->Show();

	delete pCanvas;
}

void
$(baseName)::OnBackground(void)
{
	// TODO:
	// Stop drawing when the application is moved to the background.
}

void
$(baseName)::OnLowMemory(void)
{
	// TODO:
	// Free unused resources or close the application.
}

void
$(baseName)::OnBatteryLevelChanged(BatteryLevel batteryLevel)
{
	// TODO:
	// Handle any changes in battery level here.
	// Stop using multimedia features(camera, mp3 etc.) if the battery level is CRITICAL.
}

void
$(baseName)::OnScreenOn (void)
{
	// TODO:
	// Get the released resources or resume the operations that were paused or stopped in OnScreenOff().
}

void
$(baseName)::OnScreenOff (void)
{
	// TODO:
	//  Unless there is a strong reason to do otherwise, release resources (such as 3D, media, and sensors) to allow the device to enter the sleep mode to save the battery.
	// Invoking a lengthy asynchronous method within this listener method can be risky, because it is not guaranteed to invoke a callback before the device enters the sleep mode.
	// Similarly, do not perform lengthy operations in this listener method. Any operation must be a quick one.
}
