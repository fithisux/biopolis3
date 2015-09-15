/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biopolisdata;

import javax.xml.bind.annotation.*;
import javax.validation.constraints.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisRecommendationRequest 
{
    public BiopolisSimilarity[] similarities;
    public int Ntags;
}
