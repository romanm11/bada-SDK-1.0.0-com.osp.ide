#include "$(formName).h"

using namespace Osp::Base;
using namespace Osp::Ui;
using namespace Osp::Ui::Controls;

$(formName)::$(formName)(void)
{
}

$(formName)::~$(formName)(void)
{
}

bool
$(formName)::Initialize()
{
	// Construct an XML form
	Construct(L"IDF_$(formName_upper)");

	return true;
}

result
$(formName)::OnInitializing(void)
{
	result r = E_SUCCESS;

	// TODO: Add your initialization code here

	// Get a button via resource ID
	__pButtonOk = static_cast<Button *>(GetControl(L"IDC_BUTTON_OK"));
	if (__pButtonOk != null)
	{
		__pButtonOk->SetActionId(ID_BUTTON_OK);
		__pButtonOk->AddActionEventListener(*this);
	}

	return r;
}

result
$(formName)::OnTerminating(void)
{
	result r = E_SUCCESS;

	// TODO: Add your termination code here

	return r;
}

void
$(formName)::OnActionPerformed(const Osp::Ui::Control& source, int actionId)
{
	switch(actionId)
	{
	case ID_BUTTON_OK:
		{
			AppLog("OK Button is clicked! \n");
		}
		break;
	default:
		break;
	}
}


