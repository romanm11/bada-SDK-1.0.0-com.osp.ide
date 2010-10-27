/**
 * This file contains the exported symbol.
 */
#include "$(baseName).h"

// This is the constructor of a class that has been exported.
// See $(baseName).h for the class declaration.
Ospc$(baseName)::Ospc$(baseName)(void)
{
	return;
}

// This is an example of an exported method.
int Ospf$(baseName)(void)
{
	result r = E_SUCCESS;
	return static_cast<int>(r);
}
