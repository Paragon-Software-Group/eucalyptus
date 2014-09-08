// -*- mode: C; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil -*-
// vim: set softtabstop=4 shiftwidth=4 tabstop=4 expandtab:

/*************************************************************************
 * Copyright 2009-2012 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 *
 * This file may incorporate work covered under the following copyright
 * and permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms,
 *   with or without modification, are permitted provided that the
 *   following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 *   THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 *   COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 *   AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 *   SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 *   WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 *   REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 *   IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 *   NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/

//!
//! @file node/test.c
//! Need to provide description
//!

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                  INCLUDES                                  |
 |                                                                            |
\*----------------------------------------------------------------------------*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <eucalyptus.h>
#include <misc.h>
#include <data.h>
#include <euca_string.h>

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                  DEFINES                                   |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                  TYPEDEFS                                  |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                ENUMERATIONS                                |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                 STRUCTURES                                 |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                             EXTERNAL VARIABLES                             |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/* Should preferably be handled in header file */

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                              GLOBAL VARIABLES                              |
 |                                                                            |
\*----------------------------------------------------------------------------*/

const char *euca_this_component_name = "nc";    //!< Eucalyptus Component Name

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                              STATIC VARIABLES                              |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                              STATIC PROTOTYPES                             |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                                   MACROS                                   |
 |                                                                            |
\*----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------*\
 |                                                                            |
 |                               IMPLEMENTATION                               |
 |                                                                            |
\*----------------------------------------------------------------------------*/

//!
//! Execute a system command.
//!
//! @param[in] command the system command to execute
//!
void test_command(char *command)
{
    char *result = system_output(command);
    int max = 160;

    if (result && strlen(result) > max) {
        result[max - 4] = '.';
        result[max - 3] = '.';
        result[max - 2] = '.';
        result[max - 1] = 0;
    }
    printf("--->%s executed\noutput=[%s]\n\n", command, result);
    EUCA_FREE(result);
}

//!
//! Main entry point of the application
//!
//! @param[in] argc the number of parameter passed on the command line
//! @param[in] argv the list of arguments
//!
//! @return Always return 0
//!
int main(int argc, char **argv)
{
    printf("=====> testing misc.c\n");

    test_command("date");
    test_command("ls / -l | sort");
    test_command("/foo");
    {
        char c = 0;
        long l = 0;
        int i = 0;
        long long ll = 0;

        euca_lscanf("a1\na\na2\n", "a%d", &i);
        assert(i == 1);
        euca_lscanf("a\nab3\na   4\na5", "a %d", &i);
        assert(i == 4);
        euca_lscanf("", "%d", &i);
        euca_lscanf("\n\n\n", "%d", &i);
        euca_lscanf("abcdefg6", "g%d", &i);
        assert(i != 6);
        euca_lscanf("abcdefg", "ab%cdefg", &c);
        assert(c == 'c');
        euca_lscanf("a\na    7\na\n", "a %ld", &l);
        assert(l == 7L);
        euca_lscanf("a\n8a\na9\n", "a %lld", &ll);
        assert(ll == 9L);
    }

    printf("=====> testing data.c\n");
    {
#define INSTS 50
        bunchOfInstances *bag = NULL;
        ncInstance *inst = NULL;
        ncInstance *Insts[INSTS];
        int i, n;

        printf("========> testing instance struct management\n");
        free_instance(NULL);
        free_instance(&inst);
        inst = allocate_instance("the-uuid", "i1", NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0);
        assert(inst != NULL);
        free_instance(&inst);
        assert(inst == NULL);

        n = total_instances(&bag);
        assert(n == 0);
        bag = NULL;

        inst = find_instance(&bag, "foo");
        assert(inst == NULL);
        bag = NULL;

        n = remove_instance(&bag, NULL);
        assert(n != EUCA_OK);
        bag = NULL;

        for (i = 0; i < INSTS; i++) {
            char id[10];
            sprintf(id, "i-%d", i);
            inst = Insts[i] = allocate_instance("the-uuid", id, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0);
            assert(inst != NULL);
            n = add_instance(&bag, inst);
            assert(n == EUCA_OK);
        }
        n = total_instances(&bag);
        assert(n == INSTS);
        n = remove_instance(&bag, Insts[0]);
        assert(n == EUCA_OK);
        n = remove_instance(&bag, Insts[INSTS - 1]);
        assert(n == EUCA_OK);
        n = total_instances(&bag);
        assert(n == INSTS - 2);

        printf("========> testing volume struct management\n");
        ncVolume *v;
        inst = allocate_instance("the-uuid", "i2", NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0);
        assert(inst != NULL);
        for (i = 0; i < EUCA_MAX_VOLUMES; i++) {
            char id[10];
            sprintf(id, "v-%d", i);
            v = save_volume(inst, id, "tok", "rd", "ld", "ldr", VOL_STATE_ATTACHED);
            assert(v != NULL);
        }
        assert(is_volume_used(v));
        assert(save_volume(inst, "too-much", "tok", "rd", "ld", "ldr", VOL_STATE_ATTACHED) == NULL);
        assert(save_volume(inst, v->volumeId, NULL, NULL, NULL, NULL, NULL) != NULL);
        assert(save_volume(inst, v->volumeId, NULL, "RD", NULL, NULL, NULL) != NULL);
        assert(save_volume(inst, v->volumeId, NULL, NULL, "LD", NULL, NULL) != NULL);
        assert(save_volume(inst, v->volumeId, NULL, NULL, NULL, "LDR", NULL) != NULL);
        assert(save_volume(inst, v->volumeId, NULL, NULL, NULL, NULL, VOL_STATE_DETACHED) != NULL);
        assert(strcmp(v->attachmentToken, "RD") == 0);
        assert(save_volume(inst, "v-x1", NULL, NULL, NULL, NULL, VOL_STATE_ATTACHING) != NULL);
        assert(save_volume(inst, "v-x2", NULL, NULL, NULL, NULL, VOL_STATE_ATTACHING) == NULL);
        assert(save_volume(inst, "v-x1", NULL, NULL, NULL, NULL, VOL_STATE_DETACHING) != NULL);
        assert(save_volume(inst, "v-x2", NULL, NULL, NULL, NULL, VOL_STATE_ATTACHING) == NULL);
        assert(save_volume(inst, "v-x1", NULL, NULL, NULL, NULL, VOL_STATE_DETACHING_FAILED) != NULL);
        assert(save_volume(inst, "v-x2", NULL, NULL, NULL, NULL, VOL_STATE_ATTACHING) == NULL);
        assert(free_volume(inst, "v-x1") != NULL);
        for (i = 0; i < EUCA_MAX_VOLUMES - 1; i++) {
            char id[10];
            sprintf(id, "v-%d", i);
            v = free_volume(inst, id);
            assert(v != NULL);
        }
        free_instance(&inst);
        assert(inst == NULL);
    }

    printf("OK\n");
    return 0;
}
