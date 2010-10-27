#ifndef _$(baseName_upper)_H_
#define _$(baseName_upper)_H_

/**
 * This header file is included to define _EXPORT_.
 */
#include <FBase.h>

#ifdef __cplusplus
extern "C"
{
#endif // __cplusplus

// This class is exported from Projectname.so
class _EXPORT_ Ospc$(baseName) {
public:
	Ospc$(baseName)(void);
	// TODO: Add your methods here
};

// This method is exported from Projectname.so
_EXPORT_ int Ospf$(baseName)(void);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif

