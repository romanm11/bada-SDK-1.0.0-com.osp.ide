#ifndef _$(formName_upper)_H_
#define _$(formName_upper)_H_

#include <FBase.h>
#include <FUi.h>

class $(formName) :
	public Osp::Ui::Controls::Form,
	public Osp::Ui::IActionEventListener
{

// Construction
public:
	$(formName)(void);
	virtual ~$(formName)(void);
	bool Initialize(void);

// Implementation
protected:
	static const int ID_BUTTON_OK = 101;
	Osp::Ui::Controls::Button *__pButtonOk;

public:
	virtual result OnInitializing(void);
	virtual result OnTerminating(void);
	virtual void OnActionPerformed(const Osp::Ui::Control& source, int actionId);
};

#endif	//_$(formName_upper)_H_
